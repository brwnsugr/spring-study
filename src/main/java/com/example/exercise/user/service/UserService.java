package com.example.exercise.user.service;

import com.example.exercise.user.domain.User;

public interface UserService {
  void add(User user);
  void upgradeLevels();
}
