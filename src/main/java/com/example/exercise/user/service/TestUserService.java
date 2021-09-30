package com.example.exercise.user.service;

import com.example.exercise.user.dao.UserDao;
import com.example.exercise.user.domain.User;
import org.springframework.transaction.PlatformTransactionManager;

public class TestUserService extends UserService{

  private String id;
  private UserDao userDao;


  public TestUserService(String id, UserDao userDao, PlatformTransactionManager transactionManager) {
    super(userDao, transactionManager);
    this.id = id;
  }

  protected void upgradeLevel(User user) {
    if(user.getId().equals(this.id)) throw new TestUserServiceException();
    super.upgradeLevel(user);
  }
}
