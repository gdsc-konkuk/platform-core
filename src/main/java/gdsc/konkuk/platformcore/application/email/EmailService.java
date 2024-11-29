package gdsc.konkuk.platformcore.application.email;

import static gdsc.konkuk.platformcore.application.email.EmailServiceHelper.findEmailTaskById;

import gdsc.konkuk.platformcore.application.email.exceptions.EmailAlreadyProcessedException;
import gdsc.konkuk.platformcore.application.email.exceptions.EmailErrorCode;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.domain.email.repository.EmailTaskRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailService {

  private final EmailTaskRepository emailTaskRepository;

  public List<EmailTask> getAllTaskAsList() {
    return emailTaskRepository.findAll();
  }

  public EmailTask getTaskDetails(Long taskId) {
    return findEmailTaskById(emailTaskRepository, taskId);
  }

  public List<EmailTask> getAllTaskWhereNotSent() {
    return emailTaskRepository.findAllWhereNotSent();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public EmailTask registerTask(EmailTask emailTask) {
    return emailTaskRepository.save(emailTask);
  }

  @Transactional
  public EmailTask update(Long emailId, EmailSendRequest request) {
    EmailTask task = findEmailTaskById(emailTaskRepository, emailId);

    validateEmailTaskAlreadySent(task);

    task.changeEmailDetails(request.toEmailDetails());
    task.changeSendAt(request.getSendAt());

    Set<EmailReceiver> newReceivers = request.toEmailReceivers();
    Set<EmailReceiver> updatedReceivers = mergeReceivers(task, newReceivers);
    task.changeEmailReceivers(updatedReceivers);
    return task;
  }

  @Transactional
  public void markAsCompleted(Long emailTaskId) {
      EmailTask emailTask =findEmailTaskById(emailTaskRepository, emailTaskId);
      emailTask.markAsSent();
  }

  @Transactional
  public void delete(Long emailId) {
    EmailTask task = findEmailTaskById(emailTaskRepository, emailId);
    emailTaskRepository.delete(task);
  }

  @Transactional
  public void deleteAll(List<Long> emailIds) {
    List<EmailTask> tasks = emailTaskRepository.findAllById(emailIds);
    emailTaskRepository.deleteAllInBatch(tasks);
  }

  private void validateEmailTaskAlreadySent(EmailTask emailTask) {
    if (emailTask.isSent()) {
      throw EmailAlreadyProcessedException.of(EmailErrorCode.EMAIL_ALREADY_PROCESSED);
    }
  }

  private Set<EmailReceiver> mergeReceivers(EmailTask emailTask, Set<EmailReceiver> updatedReceivers) {
    List<EmailReceiver> receiversInPrevSet = emailTask.filterReceiversInPrevSet(updatedReceivers);
    List<EmailReceiver> receiversInNewSet = emailTask.filterReceiversNotInPrevSet(updatedReceivers);
    Set<EmailReceiver> mergedReceiver = new HashSet<>(receiversInPrevSet);
    mergedReceiver.addAll(receiversInNewSet);
    return mergedReceiver;
  }
}
