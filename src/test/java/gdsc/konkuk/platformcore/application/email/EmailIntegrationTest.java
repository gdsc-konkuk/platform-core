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

import gdsc.konkuk.platformcore.application.email.dtos.EmailReceiverInfo;
import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskUpsertCommand;
import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskInfo;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.repository.EmailReceiverRepository;
import gdsc.konkuk.platformcore.domain.email.repository.EmailTaskRepository;
import gdsc.konkuk.platformcore.external.discord.DiscordClient;
import gdsc.konkuk.platformcore.external.email.EmailClient;
import gdsc.konkuk.platformcore.external.email.exceptions.EmailSendingException;
import gdsc.konkuk.platformcore.global.exceptions.GlobalErrorCode;
import gdsc.konkuk.platformcore.global.scheduler.TaskInMemoryRepository;
import gdsc.konkuk.platformcore.global.scheduler.TaskNotFoundException;
import gdsc.konkuk.platformcore.util.fixture.email.EmailReceiverInfosFixture;
import gdsc.konkuk.platformcore.util.fixture.email.EmailSendRequestFixture;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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

    @Autowired
    private EmailReceiverRepository emailReceiverRepository;

    @AfterEach
    void tearDown() {
        emailReceiverRepository.deleteAllInBatch();
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
        Long registeredId = emailTaskFacade.register(EmailSendRequest.toCommand(emailRequest));
        int registeredReceiverNum = emailRequest.getReceiverInfos().size();


        // then
        assertNotNull("should return PK id of registered task", registeredId);
        assertNotNull(
                "Task Not Processed must remain in TaskRepository",
                taskInMemoryRepository.getTask(String.valueOf(registeredId)));
        assertEquals(registeredReceiverNum, emailReceiverRepository.findEmailReceiversByEmailTaskId(registeredId).size());
    }

    @Test
    @DisplayName("등록된 작업 스케줄된 시간에 실행 성공")
    void should_send_task_when_time_is_up() throws InterruptedException {
        // given
        EmailSendRequest emailRequest = EmailSendRequestFixture.builder()
                .sendAt(LocalDateTime.now().plusSeconds(5)).build()
                .getFixture();

        // when
        emailTaskFacade.register(EmailSendRequest.toCommand(emailRequest));
        sleep(10_000);

        // then
        verify(emailClient).sendEmailToReceivers(any(EmailTaskInfo
            .class));
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
        EmailTaskUpsertCommand command = EmailSendRequest.toCommand(
                EmailSendRequestFixture.builder()
                        .sendAt(LocalDateTime.now().plusMinutes(30)).build()
                        .getFixture());

        Long registeredId = emailTaskFacade.register(command);
        assertEquals(1, executor.getQueue().size());
        assertEquals(1, taskInMemoryRepository.size());

        EmailSendRequest updatedRequest = EmailSendRequestFixture.builder()
                .sendAt(LocalDateTime.now().plusHours(1)).build()
                .getFixture();

        // when
        emailTaskFacade.update(registeredId, EmailSendRequest.toCommand(updatedRequest));

        // then
        assertEquals(1, executor.getQueue().size());
        assertNotNull(
                "Task Not Processed must remain in TaskRepository",
                taskInMemoryRepository.getTask(String.valueOf(registeredId))
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
        Long registeredTaskId = emailTaskFacade.register(EmailSendRequest.toCommand(emailRequest));
        assertEquals(1, executor.getQueue().size());

        // when
        emailTaskFacade.cancel(registeredTaskId);

        // then
        assertEquals(0, executor.getQueue().size());
        assertTrue(emailTaskRepository.findById(registeredTaskId).isEmpty());
        assertThrows(
                TaskNotFoundException.class,
                () -> taskInMemoryRepository.getTask(registeredTaskId.toString()));
    }

    @Test
    void should_send_discord_message_when_email_sending_error() throws InterruptedException {
        //given
        EmailSendRequest emailRequest = EmailSendRequestFixture.builder()
                .sendAt(LocalDateTime.now().plusSeconds(1)).build()
                .getFixture();

        doThrow(EmailSendingException.of(GlobalErrorCode.INTERNAL_SERVER_ERROR))
                .when(emailClient).sendEmailToReceivers(any(EmailTaskInfo
                .class));

        //when
        emailTaskFacade.register(EmailSendRequest.toCommand(emailRequest));
        sleep(2000);

        //then
        verify(emailClient).sendEmailToReceivers(any(EmailTaskInfo
            .class));
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
        Long registeredId1 = emailTaskFacade.register(EmailSendRequest.toCommand(emailRequest1));
        Long registeredId2 = emailTaskFacade.register(EmailSendRequest.toCommand(emailRequest2));
        assertEquals(2, executor.getQueue().size());

        // when
        emailTaskFacade.cancelAll(List.of(registeredId1, registeredId2));

        // then
        assertEquals(0, executor.getQueue().size());
        assertTrue(emailTaskRepository.findById(registeredId1).isEmpty());
        assertTrue(emailTaskRepository.findById(registeredId2).isEmpty());
        assertThrows(
                TaskNotFoundException.class,
                () -> taskInMemoryRepository.getTask(registeredId1.toString()));
        assertThrows(
                TaskNotFoundException.class,
                () -> taskInMemoryRepository.getTask(registeredId2.toString()));
    }

    /*
     * 1. 작업 예약
     * 2. 전송 대상자 변경
     * 3. 대상자 삭제 및 추가 확인
     * */
    @Test
    @DisplayName("작업 전송 대상자 수정 성공")
    void should_success_when_update_target_receiver() {
        // given
        EmailSendRequest emailRequest = EmailSendRequestFixture.builder()
            .receiverInfos(
                    EmailReceiverInfosFixture.builder()
                        .emailReceiverInfos(
                            Set.of(
                            EmailReceiverInfo.builder()
                                .email("ex1@gmail.com")
                                .name("guest1")
                                .build(),
                            EmailReceiverInfo.builder()
                                .email("ex2@gmail.com")
                                .name("guest2")
                                .build())
                        ).build().getFixture())
            .sendAt(LocalDateTime.now().plusHours(1)).build()
            .getFixture();
        Long registeredId = emailTaskFacade.register(EmailSendRequest.toCommand(emailRequest));

        EmailSendRequest updatedRequest = EmailSendRequestFixture.builder()
            .receiverInfos(
                EmailReceiverInfosFixture.builder()
                    .emailReceiverInfos(
                        Set.of(
                            EmailReceiverInfo.builder()
                                .email("ex3@gmail.com")
                                .name("guest3")
                                .build())
                    ).build().getFixture())
            .sendAt(LocalDateTime.now().plusHours(1)).build()
            .getFixture();

        // when
        emailTaskFacade.update(registeredId, EmailSendRequest.toCommand(updatedRequest));

        // then
        var updatedReceivers = emailReceiverRepository.findEmailReceiversByEmailTaskId(registeredId);
        assertEquals(updatedRequest.getReceiverInfos().size(), updatedReceivers.size());
        assertEquals("ex3@gmail.com", updatedReceivers.get(0).getEmail());
        assertEquals("guest3", updatedReceivers.get(0).getName());
    }

}
