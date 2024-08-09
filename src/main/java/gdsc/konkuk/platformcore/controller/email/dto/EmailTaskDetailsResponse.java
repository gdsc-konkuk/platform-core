package gdsc.konkuk.platformcore.controller.email.dto;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetails;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailTaskDetailsResponse {
  private final String subject;
  private final String content;
  private final List<String> receivers;
  private final LocalDateTime sendAt;

  @Builder
  public EmailTaskDetailsResponse(EmailDetails emailDetails, EmailReceivers emailReceivers, LocalDateTime sendAt) {
    this.subject = emailDetails.getSubject();
    this.content = emailDetails.getContent();
    this.receivers = emailReceivers
        .getReceivers()
        .stream()
        .toList();
    this.sendAt = sendAt;
  }
}
