package com.example.exercise.proxy;

import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProxyTest {

  @Test
  public void simpleProxy() throws Exception {
    Hello hello = new HelloTarget();
    Assertions.assertEquals(hello.sayHello("Toby"), "Hello Toby");
    Assertions.assertEquals(hello.sayHi("Toby"), "Hi Toby");
    Assertions.assertEquals(hello.sayThankYou("Toby"), "Thank you Toby");
  }

  @Test
  public void HelloUppercaseProxyTest() throws Exception {
//    Hello proxiedHello = new HelloUppercase(new HelloTarget());
    Hello proxiedHello = (Hello) Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class[] {Hello.class},
        new UppercaseHandler(new HelloTarget())
    );
    Assertions.assertEquals(proxiedHello.sayHello("Toby"), "HELLO TOBY");
    Assertions.assertEquals(proxiedHello.sayHi("Toby"), "HI TOBY");
    Assertions.assertEquals(proxiedHello.sayThankYou("Toby"), "THANK YOU TOBY");
  }

}
