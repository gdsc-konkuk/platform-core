package gdsc.konkuk.platformcore.controller.email.dto;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetails;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SimpleEmailTaskResponse {

  private final Long id;
  private final String subject;
  private final String receiver;
  private final LocalDateTime sendAt;
  private final Boolean isSent;

  @Builder
  public SimpleEmailTaskResponse(Long id, String subject, String receiver, LocalDateTime sendAt, boolean isSent) {
    this.id = id;
    this.subject = subject;
    this.receiver = receiver;
    this.sendAt = sendAt;
    this.isSent = isSent;
  }

  public static SimpleEmailTaskResponse of(EmailTask emailTask, String receiver) {
    EmailDetails emailDetails = emailTask.getEmailDetails();
    return SimpleEmailTaskResponse.builder()
        .id(emailTask.getId())
        .subject(emailDetails.getSubject())
        .receiver(receiver)
        .sendAt(emailTask.getSendAt())
        .isSent(emailTask.isSent())
        .build();
  }
}
