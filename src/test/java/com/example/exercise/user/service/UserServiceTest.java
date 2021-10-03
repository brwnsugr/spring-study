package com.example.exercise.user.service;

import static com.example.exercise.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.exercise.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;
import static org.junit.jupiter.api.Assertions.fail;

import com.example.exercise.ExerciseApplication;
import com.example.exercise.proxy.TransactionHandler;
import com.example.exercise.user.dao.UserDao;
import com.example.exercise.user.domain.Level;
import com.example.exercise.user.domain.User;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest(classes = ExerciseApplication.class)
public class UserServiceTest {

  @Autowired
  UserServiceImpl userServiceImpl;

  @Autowired
  UserDao userDao;

  @Autowired
  DataSource dataSource;

  @Autowired
  PlatformTransactionManager transactionManager;

  @Autowired MailSender mailSender;


  List<User> users;

  @BeforeEach
  public void setUp() {
    users = Arrays.asList(
        new User("bumjin", "bumjini", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
        new User("joytouch", "myeongseong", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
        new User("erwins", "seounghan", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1),
        new User("madnite1", "sangho", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
        new User("green", "mingyu", "p5", Level.GOLD, 100, 100)
    );
  }

  @Test
  public void bean(){
    Assertions.assertThat(this.userServiceImpl).isNotNull();
  }

  @Test
  public void add(){
    userDao.deleteAll();

    User userWithLevel = users.get(4);
    User userWithoutLevel = users.get(0);
    userWithoutLevel.setLevel(null);

    userServiceImpl.add(userWithLevel);
    userServiceImpl.add(userWithoutLevel);

    User userWithLevelRead = userDao.get(userWithLevel.getId());
    User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

    Assertions.assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
    Assertions.assertThat(userWithoutLevelRead.getLevel()).isEqualTo(userWithoutLevel.getLevel());
  }

  @Test
  public void upgradeLevels() throws SQLException {

    MockUserDao mockUserDao = new MockUserDao(this.users);
    userServiceImpl.setUserDao(mockUserDao);

    MockMailSender mockMailSender = new MockMailSender();
    userServiceImpl.setMailSender(mockMailSender);

    userServiceImpl.upgradeLevels();

    List<User> updated = mockUserDao.getUpdated();
    Assertions.assertThat(updated.size()).isEqualTo(2);
    checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
    checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

    List<String> request = mockMailSender.getRequests();
    Assertions.assertThat(request.size()).isEqualTo(2);
    Assertions.assertThat(request.get(0)).isEqualTo(users.get(1).getEmail());
    Assertions.assertThat(request.get(1)).isEqualTo(users.get(3).getEmail());
  }

  private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
    Assertions.assertThat(updated.getId()).isEqualTo(expectedId);
    Assertions.assertThat(updated.getLevel()).isEqualTo(expectedLevel);
  }

  private void checkLevelUpgraded(User user, boolean upgraded) {
    User userUpdate = userDao.get(user.getId());
    if (upgraded) {
      Assertions.assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
    }
    else {
      Assertions.assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
    }
  }

  @Test
  public void upgradeAllOrNothing() {
    UserServiceImpl testUserService = new TestUserService(users.get(3).getId(), userDao, transactionManager, mailSender);
    userDao.deleteAll();
    testUserService.setDataSource(this.dataSource);

    TransactionHandler txHandler = new TransactionHandler();

    txHandler.setTarget(testUserService);
    txHandler.setTransactionManager(transactionManager);
    txHandler.setPattern("upgradeLevels");
    //UserService 타입의 다이내믹 프록시 생성
    UserService txUserService = (UserService) Proxy.newProxyInstance(
        getClass().getClassLoader(), new Class[]{UserService.class}, txHandler
    );

    for(User user : users) userDao.add(user);

    try{
      txUserService.upgradeLevels();
      fail("TestUserServiceException expected");
    } catch(TestUserServiceException e) {
    };

    checkLevelUpgraded(users.get(1), false);
  }

  static class MockMailSender implements MailSender {
    private List<String> requests = new ArrayList<>();

    public List<String> getRequests() {
      return requests;
    }

    @Override
    public void send(SimpleMailMessage mailMessage) throws MailException {
      requests.add(mailMessage.getTo()[0]);
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {

    }
  }

  static class MockUserDao implements UserDao {

    private List<User> users;
    private List<User> updated = new ArrayList<>();

    private MockUserDao(List<User> users) {
      this.users = users;
    }

    public List<User> getUpdated() {
      return this.updated;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void add(User user) {
      throw new UnsupportedOperationException();
    }

    @Override
    public User get(String id) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<User> getAll() {
      return this.users;
    }

    @Override
    public void deleteAll() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getCount() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void update(User user) {
      updated.add(user);
    }

  }

}
