package gdsc.konkuk.platformcore.domain.email.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
            .emailReceivers(List.of()) // 빈 리스트로 초기화
            .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
            .build();

        Set<EmailReceiver> newReceivers = Set.of(
            EmailReceiver.builder()
                .emailTask(emailTask)
                .email("aaa@gmail.com")
                .name("guest a")
                .build(),
            EmailReceiver.builder()
                .emailTask(emailTask)
                .email("bbb@gmail.com")
                .name("guest b")
                .build(),
            EmailReceiver.builder()
                .emailTask(emailTask)
                .email("ccc@gmail.com")
                .name("guest c")
                .build());

        //when
        emailTask.changeEmailReceivers(newReceivers);

        //then
        assertEquals(3, emailTask.getReceivers().size());
        assertTrue(emailTask.getReceivers().stream()
            .anyMatch(r -> r.getEmail().equals("aaa@gmail.com")));
    }

    @Test
    @DisplayName("이메일 내용 수정 성공")
    void should_success_when_change_email_details() {
        // given
        EmailTask emailTask = EmailTask.builder()
            .emailDetail(EmailDetail.builder()
                .subject("예시 이메일 제목")
                .content("Html 문자열")
                .build())
            .emailReceivers(List.of()) // 수정: receivers로 변경
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
        EmailTask emailTask = EmailTask.builder()
            .emailDetail(EmailDetail.builder()
                .subject("예시 이메일 제목")
                .content("Html 문자열")
                .build())
            .emailReceivers(List.of()) // 수정: receivers로 변경
            .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
            .build();

        // when
        emailTask.markAsSent();

        // then
        assertTrue(emailTask.isSent());
    }

    @Test
    @DisplayName("일부 수신자만 완료된 경우 작업 미완료 확인")
    void should_return_false_when_some_receivers_not_completed() {
        // given
        EmailTask emailTask = createEmailTaskWithReceivers();

        // when - 첫 번째 수신자만 완료
        EmailReceiver firstReceiver = emailTask.getReceivers().get(0);
        firstReceiver.markAsPending();
        firstReceiver.markAsCompleted();

        // then
        assertFalse(emailTask.isAllCompleted());
    }

    @Test
    @DisplayName("대기 중인 수신자만 필터링")
    void should_return_waiting_receivers_only() {
        // given
        EmailTask emailTask = createEmailTaskWithReceivers();

        // when - 첫 번째 수신자를 PENDING으로 변경
        emailTask.getReceivers().get(0).markAsPending();

        // then
        List<EmailReceiver> waitingReceivers = emailTask.getWaitingReceivers();
        assertEquals(2, waitingReceivers.size()); // 3명 중 1명이 PENDING이므로 2명이 WAITING
        assertTrue(waitingReceivers.stream()
            .allMatch(r -> r.getSendStatus() == EmailSendStatus.WAITING));
    }

    @Test
    @DisplayName("완료된 수신자 수 카운트")
    void should_count_completed_receivers() {
        // given
        EmailTask emailTask = createEmailTaskWithReceivers();

        // when - 2명의 수신자를 완료 상태로 변경
        List<EmailReceiver> receivers = emailTask.getReceivers();
        receivers.get(0).markAsPending();
        receivers.get(0).markAsCompleted();
        receivers.get(1).markAsPending();
        receivers.get(1).markAsCompleted();

        // then
        assertEquals(2, emailTask.getCompletedCount());
    }

    private EmailTask createEmailTaskWithReceivers() {
        EmailTask emailTask = EmailTask.builder()
            .emailDetail(EmailDetail.builder()
                .subject("테스트 제목")
                .content("테스트 내용")
                .build())
            .emailReceivers(List.of())
            .sendAt(LocalDateTime.now().plusHours(1))
            .build();

        Set<EmailReceiver> receivers = Set.of(
            EmailReceiver.builder()
                .emailTask(emailTask)
                .email("test1@example.com")
                .name("테스터1")
                .build(),
            EmailReceiver.builder()
                .emailTask(emailTask)
                .email("test2@example.com")
                .name("테스터2")
                .build(),
            EmailReceiver.builder()
                .emailTask(emailTask)
                .email("test3@example.com")
                .name("테스터3")
                .build());

        emailTask.changeEmailReceivers(receivers);
        return emailTask;
    }
}
