package gdsc.konkuk.platformcore.application.email;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.MockitoAnnotations.*;

import gdsc.konkuk.platformcore.application.email.exceptions.EmailAlreadyProcessedException;
import gdsc.konkuk.platformcore.application.email.exceptions.EmailNotFoundException;
import gdsc.konkuk.platformcore.controller.email.dto.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailDetails;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.domain.email.repository.EmailTaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class EmailServiceTest {

  @Mock private EmailTaskRepository emailTaskRepository;

  private EmailService subject;

  private final EmailTask mock1 =
      EmailTask.builder()
          .id(1L)
          .emailDetails(new EmailDetails("subject", "content"))
          .receivers(new EmailReceivers(Set.of("example1.com", "example2.com")))
          .sendAt(LocalDateTime.of(2021, 1, 1, 1, 1))
          .build();

  private final EmailTask mock2 =
      EmailTask.builder()
          .id(2L)
          .emailDetails(new EmailDetails("subject2", "content2"))
          .receivers(new EmailReceivers(Set.of("example1.com", "example2.com")))
          .sendAt(LocalDateTime.now())
          .build();
  List<EmailTask> mockEmailTaskList = List.of(mock1, mock2);
  private final EmailTask mockAlreadySent =
      EmailTask.builder()
          .id(3L)
          .emailDetails(new EmailDetails("subject3", "content3"))
          .receivers(new EmailReceivers(Set.of("example1.com", "example2.com")))
          .sendAt(LocalDateTime.now())
          .build();

  @BeforeEach
  void setUp() {
    openMocks(this);
    subject = new EmailService(emailTaskRepository);
    mockAlreadySent.markAsSent();
  }

  @Test
  @DisplayName("getAllTaskAsList : 모든 이메일 전송 작업 목록 조회 성공")
  void should_success_when_getAllTaskAsList() {

    given(emailTaskRepository.findAll()).willReturn(mockEmailTaskList);

    // when
    List<EmailTask> actual = subject.getAllTaskAsList();
    // then
    assertEquals(mockEmailTaskList.size(), actual.size());
    assertEquals(mockEmailTaskList.get(0).getId(), actual.get(0).getId());
    assertEquals(mockEmailTaskList.get(1).getId(), actual.get(1).getId());
  }

  @Test
  @DisplayName("getTaskDetails : 특정 이메일 전송 작업 조회 성공")
  void should_success_when_getTaskDetails() {
    // given
    given(emailTaskRepository.findById(1L)).willReturn(java.util.Optional.of(mock1));
    // when
    EmailTask actual = subject.getTaskDetails(1L);
    // then
    assertEquals(mock1.getId(), actual.getId());
    assertEquals(mock1.getEmailDetails().getSubject(), actual.getEmailDetails().getSubject());
    assertEquals(
        mock1.getEmailReceivers().getReceivers(), actual.getEmailReceivers().getReceivers());
  }

  @Test
  @DisplayName("registerTask : 이메일 전송 작업 등록 성공")
  void should_success_when_register_task() {
    // given

    EmailSendRequest emailRequest =
        EmailSendRequest.builder()
            .subject("subject")
            .content("content")
            .receivers(Set.of("example1.com", "example2.com"))
            .sendAt(LocalDateTime.of(2021, 1, 1, 1, 1))
            .build();
    given(emailTaskRepository.save(any(EmailTask.class))).willReturn(mock1);
    // when
    EmailTask expected = EmailSendRequest.toEntity(emailRequest);
    EmailTask actual = subject.registerTask(EmailSendRequest.toEntity(emailRequest));

    // then
    assertEquals(expected.getEmailDetails(), actual.getEmailDetails());
    assertEquals(expected.getEmailReceivers(), actual.getEmailReceivers());
  }

  @Test
  @DisplayName("update : 이메일 전송 작업 수정 성공")
  void should_success_when_update_task() {
    // given
    EmailSendRequest emailRequest =
        EmailSendRequest.builder()
            .subject("subject2")
            .content("content2")
            .receivers(Set.of("example2.com", "example4.com"))
            .sendAt(LocalDateTime.of(2021, 1, 1, 1, 1))
            .build();
    given(emailTaskRepository.findById(1L)).willReturn(java.util.Optional.of(mock1));
    // when
    EmailTask expected = EmailSendRequest.toEntity(emailRequest);
    EmailTask actual = subject.update(1L, emailRequest);
    // then
    assertEquals(expected.getEmailDetails(), actual.getEmailDetails());
    assertEquals(expected.getEmailReceivers(), actual.getEmailReceivers());
  }

  @Test
  @DisplayName("update : 이미 전송된 작업 수정 시도 실패")
  void should_fail_when_update_task_already_sent() {
    // given
    EmailSendRequest emailRequest =
        EmailSendRequest.builder()
            .subject("subject2")
            .content("content2")
            .receivers(Set.of("example2.com", "example4.com"))
            .sendAt(LocalDateTime.of(2021, 1, 1, 1, 1))
            .build();
    // when
    when(emailTaskRepository.findById(1L)).thenReturn(Optional.of(mockAlreadySent));
    // then
    assertThrows(EmailAlreadyProcessedException.class, () -> subject.update(1L, emailRequest));
  }

  @Test
  @DisplayName("update : 존재하지 않는 작업 수정 시도 실패")
  void should_fail_when_update_task_not_found() {
    // given
    EmailSendRequest emailRequest =
        EmailSendRequest.builder()
            .subject("subject2")
            .content("content2")
            .receivers(Set.of("example2.com", "example4.com"))
            .sendAt(LocalDateTime.of(2021, 1, 1, 1, 1))
            .build();
    // when
    when(emailTaskRepository.findById(1L)).thenReturn(Optional.empty());
    // then
    assertThrows(EmailNotFoundException.class, () -> subject.update(1L, emailRequest));
  }

  @Test
  @DisplayName("delete : 이메일 전송 작업 삭제 성공")
  void should_success_when_delete_task() {
    // given
    given(emailTaskRepository.findById(1L)).willReturn(Optional.of(mock1));
    // when
    doNothing().when(emailTaskRepository).delete(mock1);
    subject.delete(1L);
    // then
    verify(emailTaskRepository).delete(mock1);
  }
}
