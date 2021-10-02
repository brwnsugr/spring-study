package com.example.exercise.user.dao;

import com.example.exercise.user.domain.Level;
import com.example.exercise.user.domain.User;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public interface UserDao {

  public void setDataSource(DataSource dataSource);

  public void add(final User user);

  public User get(String id);

  public List<User> getAll();

  public void deleteAll();

  public int getCount();

  public void update(User user);


}
