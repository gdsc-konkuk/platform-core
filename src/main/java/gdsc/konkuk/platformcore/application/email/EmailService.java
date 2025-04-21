package gdsc.konkuk.platformcore.application.email;

import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskUpsertCommand;
import gdsc.konkuk.platformcore.application.email.exceptions.EmailAlreadyProcessedException;
import gdsc.konkuk.platformcore.application.email.exceptions.EmailErrorCode;
import gdsc.konkuk.platformcore.application.email.exceptions.EmailNotFoundException;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.domain.email.repository.EmailTaskRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailService {

    private final EmailTaskRepository emailTaskRepository;

    public List<EmailTask> getAllTaskAsList() {
        return emailTaskRepository.findAllByOrderBySendAtDesc();
    }

    public List<EmailTask> getTasksInIds(List<Long> emailIds) {
        return emailTaskRepository.findAllById(emailIds);
    }

    public EmailTask findById(Long taskId) {
        return emailTaskRepository.findById(taskId)
            .orElseThrow(() ->EmailNotFoundException.of(EmailErrorCode.EMAIL_NOT_FOUND));
    }

    public List<EmailTask> getAllTaskWhereNotSent() {
        return emailTaskRepository.findAllWhereNotSent();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EmailTask registerTask(EmailTask emailTask) {
        return emailTaskRepository.save(emailTask);
    }

    @Transactional
    public EmailTask update(Long emailId, EmailTaskUpsertCommand command) {
        EmailTask task = findById(emailId);

        validateEmailTaskAlreadySent(task);

        task.changeEmailDetails(command.emailTaskDetail());
        task.changeSendAt(command.sendAt());

        Set<EmailReceiver> newReceivers = command.emailReceivers().getReceivers();
        Set<EmailReceiver> updatedReceivers = mergeReceivers(task, newReceivers);
        task.changeEmailReceivers(updatedReceivers);
        return task;
    }

    @Transactional
    public void markAsCompleted(Long emailTaskId) {
        EmailTask emailTask = findById(emailTaskId);
        emailTask.markAsSent();
    }

    @Transactional
    public void delete(Long emailId) {
        EmailTask task = findById(emailId);
        emailTaskRepository.delete(task);
    }

    @Transactional
    public void deleteAll(List<EmailTask> emailTasks) {
        emailTaskRepository.deleteAllInBatch(emailTasks);
    }

    private void validateEmailTaskAlreadySent(EmailTask emailTask) {
        if (emailTask.isSent()) {
            throw EmailAlreadyProcessedException.of(EmailErrorCode.EMAIL_ALREADY_PROCESSED);
        }
    }

    private Set<EmailReceiver> mergeReceivers(EmailTask emailTask,
            Set<EmailReceiver> updatedReceivers) {
        List<EmailReceiver> receiversInPrevSet = emailTask.filterReceiversInPrevSet(
                updatedReceivers);
        List<EmailReceiver> receiversInNewSet = emailTask.filterReceiversNotInPrevSet(
                updatedReceivers);
        Set<EmailReceiver> mergedReceiver = new HashSet<>(receiversInPrevSet);
        mergedReceiver.addAll(receiversInNewSet);
        return mergedReceiver;
    }
}
