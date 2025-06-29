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

    //////////// --- Command Methods ---
    @Transactional
    public EmailTask registerTask(EmailTask emailTask) {
        return emailTaskRepository.save(emailTask);
    }

    @Transactional
    public List<EmailReceiver> registerReceivers(EmailTask task, Set<EmailReceiverInfo> receivers) {
        return emailReceiverRepository.saveAll(mapToEmailReceiverList(task, receivers));
    }

    @Transactional
    public EmailTask update(Long emailId, EmailTaskUpsertCommand command) {
        EmailTask task = findById(emailId);

        validateEmailTaskAlreadySent(task);

        task.changeEmailDetails(command.emailTaskDetail());
        task.changeSendAt(command.sendAt());
        var newReceivers = new HashSet<>(mapToEmailReceiverList(task, command.emailReceiverInfos()));
        mergeEmailReceivers(task, newReceivers);
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

    private void mergeEmailReceivers(EmailTask task, Set<EmailReceiver> newReceivers) {
        var oldReceivers = emailReceiverRepository.findEmailReceiversByEmailTaskId(task.getId());
        // 지울 애들만 필터링
        var receiversToDelete = oldReceivers.stream()
            .filter(receiver -> !newReceivers.contains(receiver)).toList();
        emailReceiverRepository.deleteAllInBatch(receiversToDelete);
        emailReceiverRepository.saveAll(newReceivers);
    }

    private void validateEmailTaskAlreadySent(EmailTask emailTask) {
        if (emailTask.isSent()) {
            throw EmailAlreadyProcessedException.of(EmailErrorCode.EMAIL_ALREADY_PROCESSED);
        }
    }
}
