package gdsc.konkuk.platformcore.domain.email.entity;

import static gdsc.konkuk.platformcore.global.utils.FieldValidator.validateNotNull;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailTask {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "task_id")
  private Long id;

  @Embedded private EmailDetails emailDetails;

  @Embedded private EmailReceivers emailReceivers;

  @Column(name = "send_at")
  private LocalDateTime sendAt;

  @Column(name = "is_sent")
  private boolean isSent = false;

  @Builder
  public EmailTask(
      Long id, EmailDetails emailDetails, EmailReceivers receivers, LocalDateTime sendAt) {
    this.id = id;
    this.emailDetails = validateNotNull(emailDetails, "emailDetails");
    this.emailReceivers = validateNotNull(receivers, "emailReceivers");
    this.sendAt = validateNotNull(sendAt, "sendAt");
  }

  public void changeEmailDetails(final EmailDetails newEmailDetails) {
    emailDetails = newEmailDetails;
  }

  public void changeEmailReceivers(final EmailReceivers newEmailReceivers) {
    emailReceivers = newEmailReceivers;
  }

  public void changeSendAt(final LocalDateTime newSendAt) {
    sendAt = newSendAt;
  }

  public void markAsSent() {
    isSent = true;
  }
}