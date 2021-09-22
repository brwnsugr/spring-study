package com.example.exercise.user.dao;

import com.example.exercise.user.domain.User;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class UserDao {

  private JdbcTemplate jdbcTemplate;

  public UserDao() {
  }

  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  private RowMapper<User> userMapper = (rs, rowNum) -> {
    User user = new User();
    user.setId(rs.getString("id"));
    user.setName(rs.getString("name"));
    user.setPassword(rs.getString("password"));
    return user;
  };

  public void add(final User user) {
    this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)",
        user.getId(),
        user.getName(),
        user.getPassword());
  }

  public User get(String id) {
    return this.jdbcTemplate.queryForObject(
        "select * from users where id = ?", new Object[]{id},this.userMapper);
  }

  public List<User> getAll() {
    return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
  }

  public void deleteAll() {
    this.jdbcTemplate.update("delete from users");
  }

  public int getCount() {
    return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
  }


}
