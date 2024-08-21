package gdsc.konkuk.platformcore.controller.email.dtos;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetails;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailSendRequest {
  @NotEmpty
  private String subject;
  @NotEmpty
  private String content;
  @NotNull
  private Set<EmailReceiverInfo> receiverInfos;
  @NotNull
  private LocalDateTime sendAt;

  @Builder
  public EmailSendRequest(String subject, String content, Set<EmailReceiverInfo> receiverInfos, LocalDateTime sendAt) {
    this.subject = subject;
    this.content = content;
    this.receiverInfos = receiverInfos;
    this.sendAt = sendAt;
  }

  public static EmailTask toEntity(EmailSendRequest request) {
    EmailDetails details = new EmailDetails(request.getSubject(), request.getContent());
    EmailReceivers receivers = new EmailReceivers(request.toEmailReceivers());
    return EmailTask.builder()
      .emailDetails(details)
      .receivers(receivers)
      .sendAt(request.getSendAt())
      .build();
  }

  public EmailDetails toEmailDetails() {
    return new EmailDetails(subject, content);
  }

  public Set<EmailReceiver> toEmailReceivers() {
    return receiverInfos
            .stream()
            .map(EmailReceiverInfo::toValueObject)
            .collect(Collectors.toSet());
  }
}
