package gdsc.konkuk.platformcore.application.email;

import gdsc.konkuk.platformcore.controller.email.dto.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.global.scheduler.TaskScheduler;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailScheduleService {

  private final EmailService emailService;
  private final TaskScheduler emailTaskScheduler;

  public EmailTask scheduleEmailTask(EmailSendRequest request) {
    EmailTask emailTask = EmailSendRequest.toEntity(request);
    emailService.registerTask(emailTask);
    emailTaskScheduler.scheduleSyncTask(emailTask, getWaitingPeriod(emailTask));
    return emailTask;
  }

  public void reScheduleEmailTask(Long emailId, EmailSendRequest request) {
    emailTaskScheduler.cancelTask(String.valueOf(emailId));
    EmailTask updatedTask = emailService.update(emailId, request);
    emailTaskScheduler.scheduleSyncTask(updatedTask, getWaitingPeriod(updatedTask));
  }

  public void cancelEmailTask(Long emailId) {
    emailTaskScheduler.cancelTask(String.valueOf(emailId));
    emailService.delete(emailId);
  }

  private long getWaitingPeriod(EmailTask emailTask) {
    return ChronoUnit.SECONDS.between(LocalDateTime.now(), emailTask.getSendAt());
  }
}
