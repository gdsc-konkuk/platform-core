package gdsc.konkuk.platformcore.application.email;

import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.global.scheduler.TaskScheduler;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailTaskFacade {

    private final EmailService emailService;
    private final TaskScheduler emailTaskScheduler;

    public EmailTask register(EmailSendRequest request) {
        EmailTask emailTask = EmailSendRequest.toEntity(request);
        emailService.registerTask(emailTask);
        emailTaskScheduler.scheduleSyncTask(emailTask, getWaitingPeriod(emailTask));
        return emailTask;
    }

    public void update(Long emailId, EmailSendRequest request) {
        emailTaskScheduler.cancelTask(String.valueOf(emailId));
        EmailTask updatedTask = emailService.update(emailId, request);
        emailTaskScheduler.scheduleSyncTask(updatedTask, getWaitingPeriod(updatedTask));
    }

    public void cancel(Long emailId) {
        EmailTask savedTask = emailService.findById(emailId);
        cancelIfTaskNotProcessed(savedTask);
        emailService.delete(emailId);
    }

    public void cancelAll(List<Long> emailIds) {
        List<EmailTask> taskList = emailService.getTasksInIds(emailIds);
        cancelUnProcessedTasks(taskList);
        emailService.deleteAll(taskList);
    }

    private void cancelIfTaskNotProcessed(EmailTask emailTask) {
        if (emailTask.isSent()) {
            return;
        }
        emailTaskScheduler.cancelTask(String.valueOf(emailTask.getId()));
    }

    private void cancelUnProcessedTasks(List<EmailTask> taskList) {
        for (EmailTask task : taskList) {
            cancelIfTaskNotProcessed(task);
        }
    }

    private long getWaitingPeriod(EmailTask emailTask) {
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), emailTask.getSendAt());
    }
}
