package gdsc.konkuk.platformcore.controller.email.dtos;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetails;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SimpleEmailTaskResponse {

  private final Long id;
  private final String subject;
  private final Set<EmailReceiverInfo> receiverInfos;
  private final LocalDateTime sendAt;
  private final Boolean isSent;

  @Builder
  public SimpleEmailTaskResponse(Long id, String subject, Set<EmailReceiverInfo> receiverInfos, LocalDateTime sendAt, boolean isSent) {
    this.id = id;
    this.subject = subject;
    this.receiverInfos = receiverInfos;
    this.sendAt = sendAt;
    this.isSent = isSent;
  }

  public static SimpleEmailTaskResponse from(EmailTask emailTask) {
    EmailDetails emailDetails = emailTask.getEmailDetails();
    Set<EmailReceiverInfo> receiverInfos =
        EmailReceiverInfo.fromValueObject(emailTask.getEmailReceivers());

    return SimpleEmailTaskResponse.builder()
        .id(emailTask.getId())
        .subject(emailDetails.getSubject())
        .receiverInfos(receiverInfos)
        .sendAt(emailTask.getSendAt())
        .isSent(emailTask.isSent())
        .build();
  }
}
