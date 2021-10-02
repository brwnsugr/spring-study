package com.example.exercise.reflection;

import java.lang.reflect.Method;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReflectionTest {


  @Test
  public void invokeMethod() throws Exception {
    String name = "Spring";
    // length()
    Assertions.assertThat(name.length()).isEqualTo(6);

    Method lengthMethod = String.class.getMethod("length");
    Assertions.assertThat((Integer)lengthMethod.invoke(name)).isEqualTo(6);

    // charAt()
    Assertions.assertThat(name.charAt(0)).isEqualTo('S');

    Method charAtMethod = String.class.getMethod("charAt", int.class);
    Assertions.assertThat((Character) charAtMethod.invoke(name, 0)).isEqualTo('S');
  }
}
