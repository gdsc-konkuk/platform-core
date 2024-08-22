package gdsc.konkuk.platformcore.application.email;

import static gdsc.konkuk.platformcore.fixture.email.EmailSendRequestFixture.getEmailTask1SendRequestFixture;
import static gdsc.konkuk.platformcore.fixture.email.EmailSendRequestFixture.getEmailTask2SendRequestFixture;
import static gdsc.konkuk.platformcore.fixture.email.EmailTaskFixture.EMAIL_TASK_1_ID;
import static gdsc.konkuk.platformcore.fixture.email.EmailTaskFixture.getEmailTaskAlreadySentFixture;
import static gdsc.konkuk.platformcore.fixture.email.EmailTaskFixture.getEmailTaskFixture1;
import static gdsc.konkuk.platformcore.fixture.email.EmailTaskFixture.getEmailTaskListFixture;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.MockitoAnnotations.*;

import gdsc.konkuk.platformcore.application.email.exceptions.EmailAlreadyProcessedException;
import gdsc.konkuk.platformcore.application.email.exceptions.EmailNotFoundException;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.domain.email.repository.EmailTaskRepository;
import gdsc.konkuk.platformcore.fixture.email.EmailTaskFixture;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class EmailServiceTest {

  @Mock private EmailTaskRepository emailTaskRepository;

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
    List<EmailTask> emailTaskListToFind = getEmailTaskListFixture();
    given(emailTaskRepository.findAll()).willReturn(emailTaskListToFind);

    // when
    List<EmailTask> actual = subject.getAllTaskAsList();

    // then
    assertEquals(emailTaskListToFind.size(), actual.size());
    for(int i = 0; i < actual.size(); i++) {
      assertEquals(emailTaskListToFind.get(i).getId(), actual.get(i).getId());
    }
  }

  @Test
  @DisplayName("getTaskDetails : 특정 이메일 전송 작업 조회 성공")
  void should_success_when_getTaskDetails() {
    // given
    EmailTask emailTaskToFind = getEmailTaskFixture1();
    given(emailTaskRepository.findById(emailTaskToFind.getId()))
        .willReturn(java.util.Optional.of(emailTaskToFind));

    // when
    EmailTask actual = subject.getTaskDetails(emailTaskToFind.getId());

    // then
    assertEquals(emailTaskToFind.getId(), actual.getId());
    assertEquals(emailTaskToFind.getEmailDetail().getSubject(), actual.getEmailDetail().getSubject());
    assertEquals(
        emailTaskToFind.getEmailReceivers().getReceivers(),
        actual.getEmailReceivers().getReceivers());
  }

  @Test
  @DisplayName("registerTask : 이메일 전송 작업 등록 성공")
  void should_success_when_register_task() {
    // given
    EmailSendRequest emailRegisterRequest = getEmailTask1SendRequestFixture();
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
    EmailSendRequest emailUpdateRequest = getEmailTask2SendRequestFixture();
    EmailTask emailTaskToUpdate = getEmailTaskFixture1();
    given(emailTaskRepository.findById(EmailTaskFixture.EMAIL_TASK_1_ID))
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
    EmailTask emailTaskAlreadySent = getEmailTaskAlreadySentFixture();
    EmailSendRequest emailRequest = getEmailTask1SendRequestFixture();

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
    EmailSendRequest emailRequest = getEmailTask1SendRequestFixture();

    // when
    when(emailTaskRepository.findById(EMAIL_TASK_1_ID))
        .thenReturn(Optional.empty());

    // then
    assertThrows(
        EmailNotFoundException.class,
        () -> subject.update(EMAIL_TASK_1_ID, emailRequest));
  }

  @Test
  @DisplayName("delete : 이메일 전송 작업 삭제 성공")
  void should_success_when_delete_task() {
    // given
    EmailTask emailTaskToCancel = getEmailTaskFixture1();
    given(emailTaskRepository.findById(emailTaskToCancel.getId()))
        .willReturn(Optional.of(emailTaskToCancel));
    willDoNothing().given(emailTaskRepository).delete(emailTaskToCancel);

    // when
    subject.delete(emailTaskToCancel.getId());

    // then
    verify(emailTaskRepository).delete(emailTaskToCancel);
  }
}
