package com.example.exercise.user.service;

import com.example.exercise.user.dao.UserDao;
import com.example.exercise.user.domain.Level;
import com.example.exercise.user.domain.User;
import com.example.exercise.user.policy.BasicUpgradePolicy;
import com.example.exercise.user.policy.UserLevelUpgradePolicy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class UserService {
  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECOMMEND_FOR_GOLD = 30;

  private final UserDao userDao;
  private final UserLevelUpgradePolicy userLevelUpgradePolicy;
  private final PlatformTransactionManager transactionManager;
  private DataSource dataSource;

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public UserService(UserDao userDao, PlatformTransactionManager transactionManager) {
    this.userDao = userDao;
    this.userLevelUpgradePolicy = new BasicUpgradePolicy(userDao);
    this.transactionManager = transactionManager;
  }

  public void upgradeLevels() throws SQLException {
    TransactionStatus status = this.transactionManager
        .getTransaction(new DefaultTransactionDefinition());

    try{
      List<User> users = userDao.getAll();
      for(User user : users) {
        if(userLevelUpgradePolicy.canUpgradeLevel(user)) {
          upgradeLevel(user);
        }
      }
      this.transactionManager.commit(status); // 정상적으로 작업 마치면 커
    } catch(Exception e) {
      this.transactionManager.rollback(status);
      throw e;
    } finally {

    }
  }

  protected void upgradeLevel(User user) {
    userLevelUpgradePolicy.upgradeLevel(user);
    userDao.update(user);
  }

  public void add(User user) {
    if(user.getLevel() == null) user.setLevel(Level.BASIC);
    userDao.add(user);
  }


}
