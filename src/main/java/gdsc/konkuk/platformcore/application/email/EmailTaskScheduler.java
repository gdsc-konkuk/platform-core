package gdsc.konkuk.platformcore.application.email;

import static java.util.concurrent.TimeUnit.SECONDS;

import gdsc.konkuk.platformcore.application.email.exceptions.EmailAlreadyProcessedException;
import gdsc.konkuk.platformcore.application.email.exceptions.EmailErrorCode;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.external.email.EmailClient;
import gdsc.konkuk.platformcore.global.scheduler.TaskInMemoryRepository;
import gdsc.konkuk.platformcore.global.scheduler.TaskScheduler;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailTaskScheduler implements TaskScheduler {

  private final TaskInMemoryRepository taskInMemoryRepository;
  private final EmailService emailService;
  private final ScheduledThreadPoolExecutor executor;
  private final EmailClient emailClient;


  @Override
  public void scheduleSyncTask(Object emailTask, long delay) {

    EmailTask email = (EmailTask) emailTask;

    Runnable sendEmailTask =
        () -> {
          emailClient.sendEmailToReceivers(email);
          emailService.markAsCompleted(email.getId());
          taskInMemoryRepository.removeTask(String.valueOf(email.getId()));
        };
    ScheduledFuture<?> future = executor.schedule(sendEmailTask, delay, SECONDS);
    taskInMemoryRepository.addTask(String.valueOf(email.getId()), future);
  }

  @Override
  public synchronized void cancelTask(String taskId) {

    Future<?> scheduledFuture = taskInMemoryRepository.getTask(taskId);

    boolean isCanceled = scheduledFuture.cancel(false);

    if (!isCanceled) {
      throw EmailAlreadyProcessedException.of(EmailErrorCode.EMAIL_ALREADY_PROCESSED);
    }
    taskInMemoryRepository.removeTask(taskId);
  }

  /*
  * Read DataBase and load unsent tasks to the memory.
  * This method is called after the bean is initialized.
  * */
  @PostConstruct
  public void init() {
    log.info("EmailTaskScheduler initialized");
    List<EmailTask> tasksToReschedule = emailService.getAllTaskWhereNotSent();
    tasksToReschedule.forEach(
        task -> {
          long delay = ChronoUnit.SECONDS.between(LocalDateTime.now(), task.getSendAt());
          scheduleSyncTask(task, delay);
        });
  }
}
