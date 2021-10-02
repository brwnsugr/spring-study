package com.example.exercise.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {

  Hello target;

  public UppercaseHandler(Hello target) {
    this.target = target;
  }

  @Override
  public Object invoke(Object o, Method method, Object[] args) throws Throwable {
    Object ret = method.invoke(target, args);
    if(ret instanceof String && method.getName().startsWith("say")) {
      return ((String) ret).toUpperCase();
    }
    else {
      return ret;
    }
  }
}
