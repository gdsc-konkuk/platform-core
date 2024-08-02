package gdsc.konkuk.platformcore.application.email;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.HOURS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

import gdsc.konkuk.platformcore.application.email.exceptions.EmailAlreadyProcessedException;
import gdsc.konkuk.platformcore.controller.email.dto.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.domain.email.repository.EmailTaskRepository;
import gdsc.konkuk.platformcore.external.email.EmailClient;
import gdsc.konkuk.platformcore.global.exceptions.TaskNotFoundException;
import gdsc.konkuk.platformcore.global.scheduler.TaskInMemoryRepository;
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
  private EmailScheduleService emailScheduleService;

  @Autowired
  private EmailTaskScheduler emailTaskScheduler;

  @Autowired
  private TaskInMemoryRepository taskInMemoryRepository;

  @MockBean
  private EmailClient emailClient;

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
  * 3. 인메모리 저장소에 futuer 저장
  * */
  @Test
  @DisplayName("작업 저장 성공")
  @Transactional
  void should_save_task_at_InMemoryTaskRepository() {
    // given
    EmailSendRequest emailRequest =
        EmailSendRequest.builder()
            .subject("subject")
            .content("content")
            .receivers(List.of("example1.com", "example2.com"))
            .sendAt(LocalDateTime.now().plusHours(1))
            .build();
    // when
    EmailTask emailTask = emailScheduleService.scheduleEmailTask(emailRequest);
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
    EmailSendRequest emailRequest =
        EmailSendRequest.builder()
            .subject("subject")
            .content("content")
            .receivers(List.of("example1@gmail.com", "example2@gmail.com"))
            .sendAt(LocalDateTime.now().plusSeconds(5L))
            .build();
    // when
    emailScheduleService.scheduleEmailTask(emailRequest);
    sleep(10000);
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
    EmailSendRequest emailRequest =
        EmailSendRequest.builder()
            .subject("subject")
            .content("content")
            .receivers(List.of("example1.com", "example2.com"))
            .sendAt(LocalDateTime.now().plusHours(1))
            .build();
    EmailTask emailTask = emailScheduleService.scheduleEmailTask(emailRequest);
    assertEquals(1, executor.getQueue().size());
    assertEquals(1, taskInMemoryRepository.size());
    EmailSendRequest updatedRequest =
        EmailSendRequest.builder()
            .subject("subject")
            .content("content")
            .receivers(List.of("example1.com", "example2.com"))
            .sendAt(LocalDateTime.now().plusHours(2))
            .build();
    // when
    emailScheduleService.reScheduleEmailTask(emailTask.getId(), updatedRequest);
    // then
    assertEquals(1, executor.getQueue().size());
    assertNotNull(
        "Task Not Processed must remain in TaskRepository",
        taskInMemoryRepository.getTask(String.valueOf(emailTask.getId())).getDelay(HOURS) > 1);
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
    EmailSendRequest emailRequest =
        EmailSendRequest.builder()
            .subject("subject")
            .content("content")
            .receivers(List.of("example1.com", "example2.com"))
            .sendAt(LocalDateTime.now().plusHours(1))
            .build();
    EmailTask emailTask = emailScheduleService.scheduleEmailTask(emailRequest);
    assertEquals(1, executor.getQueue().size());

    // when
    emailScheduleService.cancelEmailTask(emailTask.getId());


    // then
    assertEquals(0, executor.getQueue().size());
    assertTrue(emailTaskRepository.findById(emailTask.getId()).isEmpty());
    assertThrows(
        TaskNotFoundException.class,
        () -> taskInMemoryRepository.getTask("1"));
  }

  @Test
  @DisplayName("이미 처리된 작업 취소 시도 시 예외 발생")
  @Transactional
  void should_fail_when_cancel_already_processed_task() throws Exception {
    //given
    EmailSendRequest emailRequest =
        EmailSendRequest.builder()
            .subject("subject")
            .content("content")
            .receivers(List.of("example1.com", "example2.com"))
            .sendAt(LocalDateTime.now().plusSeconds(1L))
            .build();
    //when
    EmailTask scheduledTask = emailScheduleService.scheduleEmailTask(emailRequest);

    sleep(2000);
    //then
    assertThrows(
        EmailAlreadyProcessedException.class,
        () -> emailScheduleService.cancelEmailTask(scheduledTask.getId()));
  }
}
