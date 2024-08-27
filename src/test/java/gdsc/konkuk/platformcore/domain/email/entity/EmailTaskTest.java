package gdsc.konkuk.platformcore.domain.email.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailTaskTest {

  @Test
  @DisplayName("이메일 수신자 리스트 수정 성공")
  void should_success_when_change_email_receivers() {
      //given
      EmailTask emailTask = EmailTask.builder()
          .emailDetail(EmailDetail.builder()
              .subject("예시 이메일 제목")
              .content("Html 문자열")
              .build())
          .receivers(new EmailReceivers(
              Set.of(
                  EmailReceiver.builder().email("example@gmail.com").name("guest1").build(),
                  EmailReceiver.builder().email("example3@gmail.com").name("guest2").build())))
          .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
          .build();
      EmailReceivers newReceivers = new EmailReceivers(
          Set.of(
              EmailReceiver.builder().email("aaa@gmail.com").name("guest a").build(),
              EmailReceiver.builder().email("bbb@gmail.com").name("guest b").build(),
              EmailReceiver.builder().email("ccc@gmail.com").name("guest c").build()));

      //when
      emailTask.changeEmailReceivers(newReceivers.getReceivers());

      //then
      assertEquals(newReceivers, emailTask.getEmailReceivers());
  }

  @Test
  @DisplayName("이메일 내용 수정 성공")
  void should_success_when_change_email_details() {
    // given
    EmailTask emailTask =
        EmailTask.builder()
            .emailDetail(EmailDetail.builder().subject("예시 이메일 제목").content("Html 문자열").build())
            .receivers(new EmailReceivers(Set.of()))
            .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
            .build();

    EmailDetail newEmailDetail =
        EmailDetail.builder().subject("newSubject").content("newContent").build();

    // when
    emailTask.changeEmailDetails(newEmailDetail);

    // then
    assertEquals(newEmailDetail, emailTask.getEmailDetail());
  }

  @Test
  @DisplayName("이메일 작업 전송 처리 성공")
  void should_success_when_mark_task_success() {
    // given
    EmailTask emailTask =
        EmailTask.builder()
            .emailDetail(EmailDetail.builder().subject("예시 이메일 제목").content("Html 문자열").build())
            .receivers(new EmailReceivers(Set.of()))
            .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
            .build();

    // when
    emailTask.markAsSent();

    // then
    assertTrue(emailTask.isSent());
  }

  @Test
  @DisplayName("수정 전 전송대상 중 겹치는 대상 추출")
  void should_success_when_get_overlapping_receivers() {
    // given
    EmailTask emailTask =
        EmailTask.builder()
            .emailDetail(EmailDetail.builder().subject("예시 이메일 제목").content("Html 문자열").build())
            .receivers(new EmailReceivers(
                Set.of(
                    EmailReceiver.builder().email("example1.com").name("guest1").build(),
                    EmailReceiver.builder().email("example2.com").name("guest2").build())))
            .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
            .build();
    Set<EmailReceiver> newEmailReceivers = Set.of(
        EmailReceiver.builder().email("example2.com").name("guest2").build(),
        EmailReceiver.builder().email("example3.com").name("guest3").build());

    // when
    List<EmailReceiver> expected = List.of(EmailReceiver.builder().email("example2.com").name("guest2").build());
    List<EmailReceiver> actual = emailTask.filterReceiversInPrevSet(newEmailReceivers);

    // then
    assertEquals(actual, expected);
  }

  @Test
  @DisplayName("수정된 전송 대상 중 이전과 겹치지 않는 대상 추출")
  void should_success_when_get_receivers_not_in_prev_set() {
    // given
    EmailTask emailTask =
        EmailTask.builder()
            .emailDetail(EmailDetail.builder().subject("예시 이메일 제목").content("Html 문자열").build())
            .receivers(new EmailReceivers(
                Set.of(
                  EmailReceiver.builder().email("example1.com").name("guest1").build(),
                  EmailReceiver.builder().email("example2.com").name("guest2").build())))
            .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
            .build();
    Set<EmailReceiver> newEmailReceivers = Set.of(
        EmailReceiver.builder().email("example2.com").name("guest2").build(),
        EmailReceiver.builder().email("example3.com").name("guest3").build());

    // when
    List<EmailReceiver> expected = List.of(
        EmailReceiver.builder().email("example3.com").name("guest3").build());
    List<EmailReceiver> actual = emailTask.filterReceiversNotInPrevSet(newEmailReceivers);

    // then
    assertEquals(actual, expected);
  }
}
