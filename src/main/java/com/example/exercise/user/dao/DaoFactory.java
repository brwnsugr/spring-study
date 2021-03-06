package com.example.exercise.user.dao;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class DaoFactory {

  @Bean
  public UserDao userDao() {
    UserDao userDao = new UserDaoImpl();
    userDao.setDataSource(dataSource());
    return userDao;
  }

//  @Bean
//  public ConnectionMaker connectionMaker() {
//    return new DConnectionMaker();
//  }

  @Bean
  public DataSource dataSource() {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
    dataSource.setUrl("jdbc:mysql://localhost:13306/exercise");
    dataSource.setUsername("root");
    dataSource.setPassword("");

    return dataSource;
  }

}
