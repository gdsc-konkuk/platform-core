package gdsc.konkuk.platformcore.application.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.MockitoAnnotations.openMocks;

import gdsc.konkuk.platformcore.application.email.exceptions.EmailAlreadyProcessedException;
import gdsc.konkuk.platformcore.application.email.exceptions.EmailNotFoundException;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.domain.email.repository.EmailTaskRepository;
import gdsc.konkuk.platformcore.fixture.email.EmailSendRequestFixture;
import gdsc.konkuk.platformcore.fixture.email.EmailTaskFixture;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class EmailServiceTest {

    @Mock
    private EmailTaskRepository emailTaskRepository;

    private EmailService subject;

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new EmailService(emailTaskRepository);
    }

    @Test
    @DisplayName("getAllTaskAsList : 모든 이메일 전송 작업 목록 조회 성공")
    void should_success_when_getAllTaskAsList() {
        // given
        List<EmailTask> emailTaskListToFind = List.of(
                EmailTaskFixture.builder().id(1L).build().getFixture(),
                EmailTaskFixture.builder().id(2L).build().getFixture(),
                EmailTaskFixture.builder().id(3L).build().getFixture()
        );
        given(emailTaskRepository.findAll()).willReturn(emailTaskListToFind);

        // when
        List<EmailTask> actual = subject.getAllTaskAsList();

        // then
        assertEquals(emailTaskListToFind.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(emailTaskListToFind.get(i).getId(), actual.get(i).getId());
        }
    }

    @Test
    @DisplayName("getTaskDetails : 특정 이메일 전송 작업 조회 성공")
    void should_success_when_getTaskDetails() {
        // given
        EmailTask emailTaskToFind = EmailTaskFixture.builder().build().getFixture();
        given(emailTaskRepository.findById(emailTaskToFind.getId()))
                .willReturn(java.util.Optional.of(emailTaskToFind));

        // when
        EmailTask actual = subject.getTaskDetails(emailTaskToFind.getId());

        // then
        assertEquals(emailTaskToFind.getId(), actual.getId());
        assertEquals(emailTaskToFind.getEmailDetail().getSubject(),
                actual.getEmailDetail().getSubject());
        assertEquals(
                emailTaskToFind.getEmailReceivers().getReceivers(),
                actual.getEmailReceivers().getReceivers());
    }

    @Test
    @DisplayName("registerTask : 이메일 전송 작업 등록 성공")
    void should_success_when_register_task() {
        // given
        EmailSendRequest emailRegisterRequest = EmailSendRequestFixture.builder().build()
                .getFixture();
        given(emailTaskRepository.save(any(EmailTask.class)))
                .willReturn(EmailSendRequest.toEntity(emailRegisterRequest));

        // when
        EmailTask expected = EmailSendRequest.toEntity(emailRegisterRequest);
        EmailTask actual = subject.registerTask(EmailSendRequest.toEntity(emailRegisterRequest));

        // then
        assertEquals(expected.getEmailDetail(), actual.getEmailDetail());
        assertEquals(expected.getEmailReceivers(), actual.getEmailReceivers());
    }

    @Test
    @DisplayName("update : 이메일 전송 작업 수정 성공")
    void should_success_when_update_task() {
        // given
        EmailSendRequest emailUpdateRequest = EmailSendRequestFixture.builder().build()
                .getFixture();
        EmailTask emailTaskToUpdate = EmailTaskFixture.builder().id(1L).build().getFixture();
        given(emailTaskRepository.findById(emailTaskToUpdate.getId()))
                .willReturn(Optional.of(emailTaskToUpdate));

        // when
        EmailTask expected = emailTaskToUpdate;
        EmailTask actual = subject.update(emailTaskToUpdate.getId(), emailUpdateRequest);

        // then
        assertEquals(expected.getEmailDetail(), actual.getEmailDetail());
        assertEquals(expected.getEmailReceivers(), actual.getEmailReceivers());
    }

    @Test
    @DisplayName("update : 이미 전송된 작업 수정 시도 실패")
    void should_fail_when_update_task_already_sent() {
        // given
        EmailTask emailTaskAlreadySent = EmailTaskFixture.builder()
                .sendAt(LocalDateTime.now().minusHours(1)).build().getFixture();
        emailTaskAlreadySent.markAsSent();
        EmailSendRequest emailRequest = EmailSendRequestFixture.builder().build().getFixture();

        // when
        when(emailTaskRepository.findById(emailTaskAlreadySent.getId()))
                .thenReturn(Optional.of(emailTaskAlreadySent));

        // then
        assertThrows(
                EmailAlreadyProcessedException.class,
                () -> subject.update(emailTaskAlreadySent.getId(), emailRequest));
    }

    @Test
    @DisplayName("update : 존재하지 않는 작업 수정 시도 실패")
    void should_fail_when_update_task_not_found() {
        // given
        EmailSendRequest emailRequest = EmailSendRequestFixture.builder().build().getFixture();

        // when
        when(emailTaskRepository.findById(0L))
                .thenReturn(Optional.empty());

        // then
        assertThrows(
                EmailNotFoundException.class,
                () -> subject.update(0L, emailRequest));
    }

    @Test
    @DisplayName("delete : 이메일 전송 작업 삭제 성공")
    void should_success_when_delete_task() {
        // given
        EmailTask emailTaskToCancel = EmailTaskFixture.builder().build().getFixture();
        given(emailTaskRepository.findById(emailTaskToCancel.getId()))
                .willReturn(Optional.of(emailTaskToCancel));
        willDoNothing().given(emailTaskRepository).delete(emailTaskToCancel);

        // when
        subject.delete(emailTaskToCancel.getId());

        // then
        verify(emailTaskRepository).delete(emailTaskToCancel);
    }
}
