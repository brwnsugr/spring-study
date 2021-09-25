package com.example.exercise.user.service;

import com.example.exercise.ExerciseApplication;
import com.example.exercise.user.domain.Level;
import com.example.exercise.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ExerciseApplication.class)
public class UserTest {

  User user;

  @BeforeEach
  public void setUp() {
    user = new User();
  }

  @Test
  public void upgradeLevel() {
    Level[] levels = Level.values();
    for(Level level : levels) {
      if(level.nextLevel() == null) continue;
      user.setLevel(level);
      user.upgradeLevel();
      Assertions.assertThat(user.getLevel()).isEqualTo(level.nextLevel());
    }
  }

  @Test
  public void cannotUpgradeLevel() throws IllegalStateException {
    org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () ->{
      Level[] levels = Level.values();
      for(Level level : levels) {
        if(level.nextLevel() != null) continue;
        user.setLevel(level);
        user.upgradeLevel();
      }
    });
  }

}
