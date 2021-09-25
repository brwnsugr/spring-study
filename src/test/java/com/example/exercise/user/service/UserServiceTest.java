package com.example.exercise.user.service;

import static com.example.exercise.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.exercise.user.service.UserService.MIN_RECOMMEND_FOR_GOLD;
import static org.junit.jupiter.api.Assertions.fail;

import com.example.exercise.ExerciseApplication;
import com.example.exercise.user.dao.UserDao;
import com.example.exercise.user.domain.Level;
import com.example.exercise.user.domain.User;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ExerciseApplication.class)
public class UserServiceTest {

  @Autowired
  UserService userService;

  @Autowired
  UserDao userDao;


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
    Assertions.assertThat(this.userService).isNotNull();
  }

  @Test
  public void add(){
    userDao.deleteAll();

    User userWithLevel = users.get(4);
    User userWithoutLevel = users.get(0);
    userWithoutLevel.setLevel(null);

    userService.add(userWithLevel);
    userService.add(userWithoutLevel);

    User userWithLevelRead = userDao.get(userWithLevel.getId());
    User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

    Assertions.assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
    Assertions.assertThat(userWithoutLevelRead.getLevel()).isEqualTo(userWithoutLevel.getLevel());
  }

  @Test
  public void upgradeLevels() {
    userDao.deleteAll();

    for(User user : users) userDao.add(user);

    userService.upgradeLevels();

    checkLevelUpgraded(users.get(0), false);
    checkLevelUpgraded(users.get(1), true);
    checkLevelUpgraded(users.get(2), false);
    checkLevelUpgraded(users.get(3), true);
    checkLevelUpgraded(users.get(4), false);
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
    UserService testUserService = new TestUserService(users.get(3).getId(), userDao);
    userDao.deleteAll();

    for(User user : users) userDao.add(user);

    try{
      testUserService.upgradeLevels();
      fail("TestUserServiceException expected");
    } catch(TestUserServiceException e) {
    };

    checkLevelUpgraded(users.get(1), false);
  }

}
