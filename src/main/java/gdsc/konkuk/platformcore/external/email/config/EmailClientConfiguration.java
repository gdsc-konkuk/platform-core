package gdsc.konkuk.platformcore.external.email.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailClientConfiguration {

  @Value("${spring.mail.host}")
  private String host;
  @Value("${spring.mail.port}")
  private Integer port;
  @Value("${spring.mail.username}")
  private String username;
  @Value("${spring.mail.password}")
  private String password;
  @Value("${spring.mail.properties.mail.transport.protocol}")
  private String protocol;
  @Value("${spring.mail.properties.mail.smtp.auth}")
  private boolean auth;
  @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
  private boolean starttlsEnable;

  @Bean
  public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);
    mailSender.setUsername(username);
    mailSender.setPassword(password);
    mailSender.setProtocol(protocol);
    mailSender.setDefaultEncoding("UTF-8");
    mailSender.setJavaMailProperties(buildSMTPProperties());
    return mailSender;
  }

  private Properties buildSMTPProperties() {
    Properties properties = new Properties();
    properties.put("mail.smtp.auth", auth);
    properties.put("mail.smtp.starttls.enable", starttlsEnable);
    return properties;
  }
}
