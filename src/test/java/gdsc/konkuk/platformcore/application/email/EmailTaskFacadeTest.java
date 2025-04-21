package gdsc.konkuk.platformcore.application.email;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskUpsertCommand;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.global.scheduler.TaskScheduler;
import gdsc.konkuk.platformcore.util.fixture.email.EmailSendRequestFixture;
import gdsc.konkuk.platformcore.util.fixture.email.EmailTaskFixture;
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

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new EmailTaskFacade(emailService, emailTaskScheduler);
    }

    @Test
    @DisplayName("예약 수정시 성공")
    void should_success_when_reschedule_task() {
        //given
        EmailSendRequest emailUpdateRequest = EmailSendRequestFixture.builder().build()
                .getFixture();
        EmailTaskUpsertCommand command = EmailSendRequest.toCommand(emailUpdateRequest);
        EmailTask emailTaskToUpdate = EmailTaskFixture.builder().id(1L).build().getFixture();
        given(emailService.update(emailTaskToUpdate.getId(), command))
                .willReturn(EmailTaskFixture.builder().id(1L).build().getFixture());

        //when
        subject.update(emailTaskToUpdate.getId(), command);

        //then
        verify(emailTaskScheduler).cancelTask(emailTaskToUpdate.getId().toString());
    }

    @Test
    @DisplayName("예약 취소시 성공")
    void should_success_when_cancel_task() {
        //given
        EmailTask emailTaskToCancel = EmailTaskFixture.builder().build().getFixture();
        willDoNothing().given(emailTaskScheduler).cancelTask(emailTaskToCancel.getId().toString());
        willDoNothing().given(emailService).delete(emailTaskToCancel.getId());
        when(emailService.findById(any())).thenReturn(emailTaskToCancel);

        //when
        subject.cancel(emailTaskToCancel.getId());

        //then
        verify(emailTaskScheduler).cancelTask(emailTaskToCancel.getId().toString());
        verify(emailService).delete(emailTaskToCancel.getId());
    }
}
