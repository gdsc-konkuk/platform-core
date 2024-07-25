package gdsc.konkuk.platformcore.external.email;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;

import gdsc.konkuk.platformcore.domain.email.entity.PlatformEmail;
import gdsc.konkuk.platformcore.external.email.exceptions.EmailSendingException;

class EmailClientTest {

  @Mock
  private JavaMailSender javaMailSender;

  private EmailClient emailClient;

  @BeforeEach
  void setUp() {
    openMocks(this);
    emailClient = new EmailClient(javaMailSender);
  }

  @Test
  @DisplayName("이메일의 내용 및 제목 형식 오류 발생")
  void should_fail_when_email_parsing() {
    //given
    PlatformEmail platformEmail = new PlatformEmail("example", "example");
    platformEmail.addReceivers(List.of("ex@gmail.com"));
    //when
    when(javaMailSender.createMimeMessage()).thenThrow(new MailParseException("error"));
    Executable result = () -> emailClient.sendEmailToReceivers(platformEmail);

    //then
    assertThrowsExactly(EmailSendingException.class, result);
  }
}