package gdsc.konkuk.platformcore.domain.email.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailTaskTest {

  @Test
  @DisplayName("이메일 수신자 리스트 수정 성공")
  void should_success_when_change_email_receivers() {
      //given
      EmailTask emailTask = EmailTask.builder()
          .emailDetails(EmailDetails.builder()
              .subject("예시 이메일 제목")
              .content("Html 문자열")
              .build())
          .receivers(new EmailReceivers(List.of("example@gmail.com", "example3@gmail.com")))
          .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
          .build();
      EmailReceivers newReceivers = new EmailReceivers(List.of("aaa@gmail.com", "bbb@gmail.com", "ccc@gmail.com"));
      //when

      emailTask.changeEmailReceivers(newReceivers);

      //then
      assertEquals(newReceivers, emailTask.getEmailReceivers());
  }

  @Test
  @DisplayName("이메일 내용 수정 성공")
  void should_success_when_change_email_details() {
    // given
    EmailTask emailTask =
        EmailTask.builder()
            .emailDetails(EmailDetails.builder().subject("예시 이메일 제목").content("Html 문자열").build())
            .receivers(new EmailReceivers(List.of()))
            .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
            .build();

    EmailDetails newEmailDetails =
        EmailDetails.builder().subject("newSubject").content("newContent").build();

    // when
    emailTask.changeEmailDetails(newEmailDetails);

    // then
    assertEquals(newEmailDetails, emailTask.getEmailDetails());
  }

  @Test
  @DisplayName("이메일 작업 전송 처리 성공")
  void should_success_when_mark_task_success() {
    // given
    EmailTask emailTask =
        EmailTask.builder()
            .emailDetails(EmailDetails.builder().subject("예시 이메일 제목").content("Html 문자열").build())
            .receivers(new EmailReceivers(List.of()))
            .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
            .build();

    // when
    emailTask.markAsSent();

    // then
    assertTrue(emailTask.isSent());
  }
}