package com.example.exercise.user.policy;

import com.example.exercise.user.domain.User;

public interface UserLevelUpgradePolicy {
  boolean canUpgradeLevel(User user);
  void upgradeLevel(User user);
}
