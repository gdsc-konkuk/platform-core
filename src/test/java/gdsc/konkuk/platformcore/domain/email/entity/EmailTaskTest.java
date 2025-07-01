package gdsc.konkuk.platformcore.domain.email.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailTaskTest {

    @Test
    @DisplayName("이메일 내용 수정 성공")
    void should_success_when_change_email_details() {
        // given
        EmailTask emailTask = EmailTask.builder()
            .emailDetail(EmailDetail.builder()
                .subject("예시 이메일 제목")
                .content("Html 문자열")
                .build())
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
            .sendAt(LocalDateTime.of(2021, 10, 10, 10, 10))
            .build();

        // when
        emailTask.markAsSent();

        // then
        assertTrue(emailTask.isSent());
    }
}
