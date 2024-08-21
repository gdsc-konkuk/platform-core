package gdsc.konkuk.platformcore.controller.email.dtos;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetails;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SimpleEmailTaskResponse {

  private final Long id;
  private final String subject;
  private final EmailReceiverInfo receiverInfos;
  private final LocalDateTime sendAt;
  private final Boolean isSent;

  @Builder
  public SimpleEmailTaskResponse(Long id, String subject, EmailReceiverInfo receiverInfos, LocalDateTime sendAt, boolean isSent) {
    this.id = id;
    this.subject = subject;
    this.receiverInfos = receiverInfos;
    this.sendAt = sendAt;
    this.isSent = isSent;
  }

  public static SimpleEmailTaskResponse of(EmailTask emailTask, EmailReceiver receiver) {
    EmailDetails emailDetails = emailTask.getEmailDetails();
    EmailReceiverInfo receiverInfo = EmailReceiverInfo.fromValueObject(receiver);
    return SimpleEmailTaskResponse.builder()
        .id(emailTask.getId())
        .subject(emailDetails.getSubject())
        .receiverInfos(receiverInfo)
        .sendAt(emailTask.getSendAt())
        .isSent(emailTask.isSent())
        .build();
  }
}
