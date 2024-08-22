package gdsc.konkuk.platformcore.application.email;

import static gdsc.konkuk.platformcore.fixture.email.EmailSendRequestFixture.getEmailTask2SendRequestFixture;
import static gdsc.konkuk.platformcore.fixture.email.EmailTaskFixture.getEmailTaskFixture1;
import static gdsc.konkuk.platformcore.fixture.email.EmailTaskFixture.getEmailTaskFixture2;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailDetail;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
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
          .emailDetail(new EmailDetail("subject", "content"))
          .receivers(new EmailReceivers(
              Set.of(
                  EmailReceiver.builder().email("example1.com").name("guest1").build(),
                  EmailReceiver.builder().email("example2.com").name("guest2").build())))
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
    EmailSendRequest emailUpdateRequest = getEmailTask2SendRequestFixture();
    EmailTask emailTaskToUpdate = getEmailTaskFixture1();
    given(emailService.update(emailTaskToUpdate.getId(), emailUpdateRequest))
        .willReturn(getEmailTaskFixture2());

    //when
    subject.update(emailTaskToUpdate.getId(), emailUpdateRequest);

    //then
    verify(emailTaskScheduler).cancelTask(emailTaskToUpdate.getId().toString());
  }

  @Test
  @DisplayName("예약 취소시 성공")
  void should_success_when_cancel_task() {
    //given
    EmailTask emailTaskToCancel = getEmailTaskFixture1();
    willDoNothing().given(emailTaskScheduler).cancelTask(emailTaskToCancel.getId().toString());
    willDoNothing().given(emailService).delete(emailTaskToCancel.getId());

    //when
    subject.cancel(emailTaskToCancel.getId());

    //then
    verify(emailTaskScheduler).cancelTask(emailTaskToCancel.getId().toString());
    verify(emailService).delete(emailTaskToCancel.getId());
  }
}
