package gdsc.konkuk.platformcore.application.email;

import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskDetailResponse;
import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskListResponse;
import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskUpsertCommand;
import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskInfo;
import gdsc.konkuk.platformcore.application.email.mapper.EmailTaskMapper;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.global.scheduler.TaskScheduler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTaskFacade {

    private final EmailService emailService;
    private final TaskScheduler emailTaskScheduler;

    @Transactional(readOnly = true)
    public EmailTaskListResponse getAllEmailTasks() {
        List<EmailTaskInfo> emailTaskWithReceivers = emailService.getAllTaskInfoAsList();
        return EmailTaskMapper.mapToEmailTaskListResponse(emailTaskWithReceivers);
    }

    @Transactional(readOnly = true)
    public EmailTaskDetailResponse getEmailTaskDetails(final Long taskId) {
        EmailTaskInfo emailTaskInfo = emailService.findByIdWithReceivers(taskId);
        return EmailTaskMapper.mapToEmailTaskDetailResponse(emailTaskInfo);
    }

    public Long register(final EmailTaskUpsertCommand command) {
        EmailTask emailTask = EmailTaskUpsertCommand.toTaskEntity(command);
        emailService.registerTask(emailTask);
        emailService.registerReceivers(emailTask, command.emailReceiverInfos());
        emailTaskScheduler.scheduleSyncTask(emailTask, emailTask.getLastWaitingPeriodInSeconds());
        return emailTask.getId();
    }

    @Transactional
    public void update(final Long emailId, final EmailTaskUpsertCommand command) {
        emailTaskScheduler.cancelTask(String.valueOf(emailId));
        EmailTask updatedTask = emailService.update(emailId, command);
        emailTaskScheduler.scheduleSyncTask(updatedTask,
            updatedTask.getLastWaitingPeriodInSeconds());
    }

    @Transactional
    public void cancel(final Long emailId) {
        EmailTask savedTask = emailService.findById(emailId);
        cancelIfTaskNotProcessed(savedTask);
        emailService.delete(emailId);
    }

    @Transactional
    public void cancelAll(final List<Long> emailIds) {
        List<EmailTask> taskList = emailService.getTasksInIds(emailIds);
        cancelUnProcessedTasks(taskList);
        emailService.deleteAll(taskList);
    }

    private void cancelIfTaskNotProcessed(final EmailTask emailTask) {
        if (emailTask.isSent()) {
            return;
        }
        emailTaskScheduler.cancelTask(String.valueOf(emailTask.getId()));
    }

    private void cancelUnProcessedTasks(final List<EmailTask> taskList) {
        for (EmailTask task : taskList) {
            cancelIfTaskNotProcessed(task);
        }
    }


}
