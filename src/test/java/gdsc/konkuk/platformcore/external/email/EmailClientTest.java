package gdsc.konkuk.platformcore.external.email;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetails;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.external.email.exceptions.EmailSendingException;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;

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
    EmailTask emailTask =
        EmailTask.builder()
            .emailDetails(EmailDetails.builder().subject("예시 이메일 제목").content("Html 문자열").build())
            .receivers(new EmailReceivers(Set.of("aaa@gmail.com")))
            .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
            .build();
    //when
    when(javaMailSender.createMimeMessage()).thenThrow(new MailParseException("error"));
    Executable result = () -> emailClient.sendEmailToReceivers(emailTask);

     //then
    assertThrowsExactly(EmailSendingException.class, result);
  }
}