package com.example.exercise.user.policy;

import static com.example.exercise.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.exercise.user.service.UserService.MIN_RECOMMEND_FOR_GOLD;

import com.example.exercise.user.dao.UserDao;
import com.example.exercise.user.domain.Level;
import com.example.exercise.user.domain.User;

public class BasicUpgradePolicy implements UserLevelUpgradePolicy {

  private final UserDao userDao;

  public BasicUpgradePolicy(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public boolean canUpgradeLevel(User user) {
    switch (user.getLevel()) {
      case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
      case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
      case GOLD: return false;
      default: throw new IllegalArgumentException("Unknown Level: " + user.getLevel());
    }
  }

  @Override
  public void upgradeLevel(User user) {
    user.upgradeLevel();
    userDao.update(user);
  }
}
