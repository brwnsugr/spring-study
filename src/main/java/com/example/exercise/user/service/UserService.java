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
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class UserService {
  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECOMMEND_FOR_GOLD = 30;

  private final UserDao userDao;
  private final UserLevelUpgradePolicy userLevelUpgradePolicy;
  private DataSource dataSource;

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public UserService(UserDao userDao) {
    this.userDao = userDao;
    this.userLevelUpgradePolicy = new BasicUpgradePolicy(userDao);
  }

  public void upgradeLevels() throws SQLException {
    TransactionSynchronizationManager.initSynchronization(); // 트랜잭션 동기화 작업의 초기화
    Connection c = DataSourceUtils.getConnection(dataSource); // 트랜잭션 동기화 저장소에 connection을 바인딩 해줌
    c.setAutoCommit(false);

    try{
      List<User> users = userDao.getAll();
      for(User user : users) {
        if(userLevelUpgradePolicy.canUpgradeLevel(user)) {
          upgradeLevel(user);
        }
      }
      c.commit(); // 정상적으로 작업 마치면 커
    } catch(Exception e) {
      c.rollback();
      throw e;
    } finally {
      DataSourceUtils.releaseConnection(c,dataSource); // 커넥션을 닫는다.
      TransactionSynchronizationManager.unbindResource(this.dataSource); // connection 바인드 해제
      TransactionSynchronizationManager.clearSynchronization();// 트랜잭션 동기화 저장소 종료
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
