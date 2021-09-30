package com.example.exercise.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;

@Configuration
public class MailFactory {

  @Bean
  public MailSender MailSender() {
    MailSender mailSender = new DummyMailSender();
    return mailSender;
  }
}
