package gdsc.konkuk.platformcore.external.email;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetails;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.external.email.exceptions.EmailSendingException;
import jakarta.mail.internet.MimeMessage;
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
    emailClient = spy(new EmailClient(javaMailSender));
  }

  @Test
  @DisplayName("이메일의 내용 및 제목 형식 오류 발생")
  void should_fail_when_email_parsing() {
     // given
    EmailTask emailTask =
        EmailTask.builder()
            .emailDetails(EmailDetails.builder().subject("예시 이메일 제목").content("Html 문자열").build())
            .receivers(new EmailReceivers(
                Set.of(EmailReceiver.builder().email("aaa@gmail.com").name("guest1").build())))
            .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
            .build();

    // when
    when(javaMailSender.createMimeMessage()).thenThrow(new MailParseException("error"));
    Executable result = () -> emailClient.sendEmailToReceivers(emailTask);

     // then
    assertThrowsExactly(EmailSendingException.class, result);
  }

  @Test
  @DisplayName("이메일 내용 중 {이름}은 수신자의 이름으로 치환되어야 함")
  void should_replace_to_receiver_name_when_name_token() {
    // given
    EmailTask emailTask =
        EmailTask.builder()
            .emailDetails(EmailDetails.builder()
                .subject("예시 이메일 제목")
                .content("안녕하세요, {이름}님 합격을 축하드립니다!. {이름}님과 함께할 수 있어 기쁩니다.")
                .build())
            .receivers(new EmailReceivers(
                Set.of(EmailReceiver.builder().email("ex@ex.com").name("guest1").build())))
            .sendAt(LocalDateTime.of(2024, 10, 10, 10, 10))
            .build();
    MimeMessage mockMimeMessage = mock(MimeMessage.class);
    given(javaMailSender.createMimeMessage()).willReturn(mockMimeMessage);
    given(emailClient.replaceNameToken(anyString(),anyString())).willReturn("");

    // when
    emailClient.sendEmailToReceivers(emailTask);

    // then
    verify(emailClient).replaceNameToken(
      eq("안녕하세요, {이름}님 합격을 축하드립니다!. {이름}님과 함께할 수 있어 기쁩니다."),
      eq("guest1"));
  }

  @Test
  @DisplayName("이메일 이름 토큰({이름}) 치환 테스트")
  void should_success_when_replace_name_token() {
    // given
    String content = "안녕하세요, {이름}님 합격을 축하드립니다!. {이름}님과 함께할 수 있어 기쁩니다.";
    String name = "guest1";

    // when
    String result = emailClient.replaceNameToken(content, name);

    // then
    assertEquals("안녕하세요, guest1님 합격을 축하드립니다!. guest1님과 함께할 수 있어 기쁩니다.", result);
  }
}
