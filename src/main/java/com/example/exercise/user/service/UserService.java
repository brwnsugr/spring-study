package com.example.exercise.user.service;

import com.example.exercise.user.dao.UserDao;
import com.example.exercise.user.domain.Level;
import com.example.exercise.user.domain.User;
import com.example.exercise.user.policy.BasicUpgradePolicy;
import com.example.exercise.user.policy.UserLevelUpgradePolicy;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECOMMEND_FOR_GOLD = 30;

  private final UserDao userDao;
  private final UserLevelUpgradePolicy userLevelUpgradePolicy;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
    this.userLevelUpgradePolicy = new BasicUpgradePolicy(userDao);
  }

  public void upgradeLevels() {
    List<User> users = userDao.getAll();
    for(User user : users) {
      if(userLevelUpgradePolicy.canUpgradeLevel(user)){
        upgradeLevel(user);
      }
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
