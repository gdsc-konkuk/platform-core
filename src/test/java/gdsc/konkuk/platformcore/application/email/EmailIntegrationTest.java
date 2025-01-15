package gdsc.konkuk.platformcore.application.email;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.HOURS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.fail;

import gdsc.konkuk.platformcore.application.email.exceptions.EmailAlreadyProcessedException;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.domain.email.repository.EmailTaskRepository;
import gdsc.konkuk.platformcore.external.discord.DiscordClient;
import gdsc.konkuk.platformcore.external.email.EmailClient;
import gdsc.konkuk.platformcore.external.email.exceptions.EmailSendingException;
import gdsc.konkuk.platformcore.fixture.email.EmailSendRequestFixture;
import gdsc.konkuk.platformcore.global.exceptions.GlobalErrorCode;
import gdsc.konkuk.platformcore.global.scheduler.TaskInMemoryRepository;
import gdsc.konkuk.platformcore.global.scheduler.TaskNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class EmailIntegrationTest {

    @Autowired
    private EmailTaskFacade emailTaskFacade;

    @Autowired
    private TaskInMemoryRepository taskInMemoryRepository;

    @MockBean
    private EmailClient emailClient;

    @MockBean
    private DiscordClient discordClient;

    @Autowired
    private ScheduledThreadPoolExecutor executor;

    @Autowired
    private EmailTaskRepository emailTaskRepository;

    @AfterEach
    void tearDown() {
        emailTaskRepository.deleteAllInBatch();
        taskInMemoryRepository.removeAll();
        executor.purge();
    }

    /*
     * 1. 데이터베이스에 이메일 정보 저장
     * 2. 스케줄러에 작업 예약
     * 3. 인메모리 저장소에 future 저장
     * */
    @Test
    @DisplayName("작업 저장 성공")
    @Transactional
    void should_save_task_at_InMemoryTaskRepository() {
        // given
        EmailSendRequest emailRequest = EmailSendRequestFixture.builder()
                .sendAt(LocalDateTime.now().plusHours(1)).build()
                .getFixture();

        // when
        EmailTask emailTask = emailTaskFacade.register(emailRequest);

        // then
        assertNotNull("Task should not be null", emailTask);
        assertNotNull(
                "Task Not Processed must remain in TaskRepository",
                taskInMemoryRepository.getTask(String.valueOf(emailTask.getId())));
    }

    @Test
    @DisplayName("등록된 작업 스케줄된 시간에 실행 성공")
    void should_send_task_when_time_is_up() throws InterruptedException {
        // given
        EmailSendRequest emailRequest = EmailSendRequestFixture.builder()
                .sendAt(LocalDateTime.now().plusSeconds(5)).build()
                .getFixture();

        // when
        emailTaskFacade.register(emailRequest);
        sleep(10_000);

        // then
        verify(emailClient).sendEmailToReceivers(any(EmailTask.class));
        assertEquals(0, executor.getQueue().size());
        assertEquals(0, taskInMemoryRepository.size());
    }

    /*
     * 1. 작업 예약
     * 2. 작업 수정 요청
     * 3. 기존의 작업 취소, 새로운 작업 예약
     * */
    @Test
    @DisplayName("작업 수정 시 스케줄된 작업 취소 후 다시 스케줄")
    void should_cancel_and_schedule_new_when_update() {
        // given
        EmailTask emailTaskToUpdate = emailTaskFacade.register(
                EmailSendRequestFixture.builder()
                        .sendAt(LocalDateTime.now().plusMinutes(30)).build()
                        .getFixture());
        assertEquals(1, executor.getQueue().size());
        assertEquals(1, taskInMemoryRepository.size());

        EmailSendRequest updatedRequest = EmailSendRequestFixture.builder()
                .sendAt(LocalDateTime.now().plusHours(1)).build()
                .getFixture();

        // when
        emailTaskFacade.update(emailTaskToUpdate.getId(), updatedRequest);

        // then
        assertEquals(1, executor.getQueue().size());
        assertNotNull(
                "Task Not Processed must remain in TaskRepository",
                taskInMemoryRepository.getTask(String.valueOf(emailTaskToUpdate.getId()))
                        .getDelay(HOURS) > 1);
    }

    /*
     * 1. 작업 예약
     * 2. 작업 취소 요청
     * 3. 작업 스케줄러에 취소 후 큐와 인메모리 저장소에 작업이 없어야함
     * */
    @Test
    @DisplayName("등록된 작업 취소 성공")
    @Transactional
    void should_cancel_task() {
        // given
        EmailSendRequest emailRequest = EmailSendRequestFixture.builder()
                .sendAt(LocalDateTime.now().plusHours(1)).build()
                .getFixture();
        EmailTask emailTask = emailTaskFacade.register(emailRequest);
        assertEquals(1, executor.getQueue().size());

        // when
        emailTaskFacade.cancel(emailTask.getId());

        // then
        assertEquals(0, executor.getQueue().size());
        assertTrue(emailTaskRepository.findById(emailTask.getId()).isEmpty());
        assertThrows(
                TaskNotFoundException.class,
                () -> taskInMemoryRepository.getTask(emailTask.getId().toString()));
    }

    @Test
    void should_send_discord_message_when_email_sending_error() throws InterruptedException {
        //given
        EmailSendRequest emailRequest = EmailSendRequestFixture.builder()
                .sendAt(LocalDateTime.now().plusSeconds(1)).build()
                .getFixture();

        doThrow(EmailSendingException.of(GlobalErrorCode.INTERNAL_SERVER_ERROR))
                .when(emailClient).sendEmailToReceivers(any(EmailTask.class));

        //when
        emailTaskFacade.register(emailRequest);
        sleep(2000);

        //then
        verify(emailClient).sendEmailToReceivers(any(EmailTask.class));
        verify(discordClient).sendErrorMessage(any());
    }

    @Test
    @DisplayName("모든 작업 취소 성공")
    void should_cancel_all_tasks() {
        // given
        EmailSendRequest emailRequest1 = EmailSendRequestFixture.builder()
                .sendAt(LocalDateTime.now().plusHours(1)).build()
                .getFixture();
        EmailSendRequest emailRequest2 = EmailSendRequestFixture.builder()
                .sendAt(LocalDateTime.now().plusHours(2)).build()
                .getFixture();
        EmailTask emailTask1 = emailTaskFacade.register(emailRequest1);
        EmailTask emailTask2 = emailTaskFacade.register(emailRequest2);
        assertEquals(2, executor.getQueue().size());

        // when
        emailTaskFacade.cancelAll(List.of(emailTask1.getId(), emailTask2.getId()));

        // then
        assertEquals(0, executor.getQueue().size());
        assertTrue(emailTaskRepository.findById(emailTask1.getId()).isEmpty());
        assertTrue(emailTaskRepository.findById(emailTask2.getId()).isEmpty());
        assertThrows(
                TaskNotFoundException.class,
                () -> taskInMemoryRepository.getTask(emailTask1.getId().toString()));
        assertThrows(
                TaskNotFoundException.class,
                () -> taskInMemoryRepository.getTask(emailTask2.getId().toString()));
    }

}
