package com.example.exercise.user.dao;

import com.example.exercise.user.domain.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

  public void add(User user) throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection c = DriverManager.getConnection("jdbc:mysql://localhost:13306/exercise", "root", "");

    PreparedStatement ps = c
        .prepareStatement("insert into users(id, name, password) values (?,?,?)");

    ps.setString(1, user.getId());
    ps.setString(2, user.getName());
    ps.setString(3, user.getPassword());

    ps.executeUpdate();

    ps.close();
    c.close();
  }

  public User get(String id) throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection c = DriverManager.getConnection("jdbc:mysql://localhost:13306/exercise", "root", "");

    PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
    ps.setString(1, id);

    ResultSet rs = ps.executeQuery();
    rs.next();
    User user = new User();
    user.setId(rs.getString("id"));
    user.setPassword(rs.getString("password"));

    rs.close();
    ps.close();
    c.close();

    return user;
  }

  public void init() throws ClassNotFoundException, SQLException {
    Class.forName("org.h2.Driver");
    Connection c = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa","");

    PreparedStatement ps = c.prepareStatement("create table users("
        + "id varchar(10) primary key,"
        + "name varchar(20) not null,"
        + "password varchar(10) not null)");

    ps.execute();
  }

  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    UserDao dao = new UserDao();

//    dao.init();

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
