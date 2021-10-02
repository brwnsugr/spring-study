package com.example.exercise.user.service;

import com.example.exercise.user.domain.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class UserServiceTx implements UserService{

  UserService userService; // 타깃 오브젝트
  PlatformTransactionManager transactionManager;

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  @Override
  public void add(User user) {
    userService.add(user); // 메소드 구현과 userService 로의 메소드 위임
  }

  @Override
  public void upgradeLevels() {
    TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

    try {
      userService.upgradeLevels();

      this.transactionManager.commit(status);
    } catch (RuntimeException e) {
      this.transactionManager.rollback(status);
      throw e;
    }

  }
}
