package com.example.exercise.user.dao;

import com.example.exercise.user.domain.User;
import java.sql.SQLException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class UserDaoTest {



  public static void main(String[] args) throws SQLException, ClassNotFoundException {

//    UserDao dao = new DaoFactory().userDao();

    ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
    UserDao dao = context.getBean("userDao", UserDao.class);

    User user = new User();
    user.setId("brwnsugr");
    user.setName("developer");
    user.setPassword("married");

    dao.add(user);

    System.out.println(user.getId() + " 등록 성공");

    User user2 = dao.get(user.getId());
    System.out.println(user2.getName());

    System.out.println(user2.getPassword());

    System.out.println(user2.getId() + " 조회 성공");
  }

}
