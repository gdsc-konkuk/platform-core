package gdsc.konkuk.platformcore.application.email;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailDetails;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.global.scheduler.TaskScheduler;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class EmailTaskFacadeTest {

  private EmailTaskFacade subject;

  @Mock
  private EmailService emailService;

  @Mock
  private TaskScheduler emailTaskScheduler;


  private final EmailTask mock1 =
      EmailTask.builder()
          .id(1L)
          .emailDetails(new EmailDetails("subject", "content"))
          .receivers(new EmailReceivers(Set.of("example1.com", "example2.com")))
          .sendAt(LocalDateTime.of(2021, 1, 1, 1, 1))
          .build();

  @BeforeEach
  void setUp() {
    openMocks(this);
    subject = new EmailTaskFacade(emailService, emailTaskScheduler);
  }

  @Test
  @DisplayName("예약 수정시 성공")
  void should_success_when_reschedule_task() {
      //given
    EmailSendRequest emailRequest =
        EmailSendRequest.builder()
            .subject("subject")
            .content("content")
            .receivers(Set.of("example1.com", "example2.com"))
            .sendAt(LocalDateTime.of(2021, 1, 1, 1, 1))
            .build();
    //when
    when(emailService.update(1L, emailRequest)).thenReturn(mock1);
    //then
    subject.update(1L, emailRequest);
    verify(emailTaskScheduler).cancelTask("1");
  }

  @Test
  @DisplayName("예약 취소시 성공")
  void should_success_when_cancel_task() {
    //given
    Long emailId = 1L;
    //when
    subject.cancel(1L);

    //then
    verify(emailTaskScheduler).cancelTask("1");
    verify(emailService).delete(emailId);
  }
}
