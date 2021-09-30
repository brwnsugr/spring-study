package com.example.exercise.user.service;

import com.example.exercise.user.dao.UserDao;
import com.example.exercise.user.domain.Level;
import com.example.exercise.user.domain.User;
import com.example.exercise.user.policy.BasicUpgradePolicy;
import com.example.exercise.user.policy.UserLevelUpgradePolicy;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
public class UserService {
  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECOMMEND_FOR_GOLD = 30;

  private final UserDao userDao;
  private final UserLevelUpgradePolicy userLevelUpgradePolicy;
  private final PlatformTransactionManager transactionManager;
  private MailSender mailSender;
  private DataSource dataSource;

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public UserService(UserDao userDao, PlatformTransactionManager transactionManager, MailSender mailSender) {
    this.userDao = userDao;
    this.userLevelUpgradePolicy = new BasicUpgradePolicy(userDao);
    this.transactionManager = transactionManager;
    this.mailSender = mailSender;
  }

  public void setMailSender(MailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void upgradeLevels() {
    TransactionStatus status = this.transactionManager
        .getTransaction(new DefaultTransactionDefinition());

    try{
      List<User> users = userDao.getAll();
      for(User user : users) {
        if(userLevelUpgradePolicy.canUpgradeLevel(user)) {
          upgradeLevel(user);
        }
      }
      this.transactionManager.commit(status); // 정상적으로 작업 마치면 커
    } catch(Exception e) {
      this.transactionManager.rollback(status);
      throw e;
    } finally {

    }
  }

  protected void upgradeLevel(User user) {
    userLevelUpgradePolicy.upgradeLevel(user);
    userDao.update(user);
    sendUpgradeEmail(user);
  }

  private void sendUpgradeEmail(User user) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(user.getEmail());
    mailMessage.setFrom("useradmin@ksug.com");
    mailMessage.setSubject("Upgrade 안내");
    mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());

    this.mailSender.send(mailMessage);
  }

  public void add(User user) {
    if(user.getLevel() == null) user.setLevel(Level.BASIC);
    userDao.add(user);
  }


}
