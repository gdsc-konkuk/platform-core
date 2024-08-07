package gdsc.konkuk.platformcore.application.email;

import static gdsc.konkuk.platformcore.application.email.EmailServiceHelper.findEmailTaskById;

import gdsc.konkuk.platformcore.application.email.exceptions.EmailAlreadyProcessedException;
import gdsc.konkuk.platformcore.application.email.exceptions.EmailErrorCode;
import gdsc.konkuk.platformcore.controller.email.dto.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.domain.email.repository.EmailTaskRepository;
import java.util.List;
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
    task.changeEmailReceivers(request.toEmailReceivers());
    task.changeSendAt(request.getSendAt());
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

  private void validateEmailTaskAlreadySent(EmailTask emailTask) {
    if (emailTask.isSent()) {
      throw EmailAlreadyProcessedException.of(EmailErrorCode.EMAIL_ALREADY_PROCESSED);
    }
  }
}
