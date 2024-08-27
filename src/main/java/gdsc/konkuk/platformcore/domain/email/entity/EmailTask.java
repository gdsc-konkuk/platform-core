package gdsc.konkuk.platformcore.domain.email.entity;

import static gdsc.konkuk.platformcore.global.utils.FieldValidator.validateNotNull;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import java.util.Set;
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

  @Embedded private EmailDetail emailDetail;

  @Embedded private EmailReceivers emailReceivers;

  @Column(name = "send_at")
  private LocalDateTime sendAt;

  @Column(name = "is_sent")
  private boolean isSent = false;

  @Builder
  public EmailTask(
      Long id, EmailDetail emailDetail, EmailReceivers receivers, LocalDateTime sendAt) {
    this.id = id;
    this.emailDetail = validateNotNull(emailDetail, "emailDetails");
    this.emailReceivers = validateNotNull(receivers, "emailReceivers");
    this.sendAt = validateNotNull(sendAt, "sendAt");
  }

  public void changeEmailDetails(final EmailDetail newEmailDetail) {
    emailDetail = newEmailDetail;
  }

  public void changeEmailReceivers(final Set<EmailReceiver> set) {
    emailReceivers.removeAll();
    emailReceivers.insertAll(set);
  }

  public void changeSendAt(final LocalDateTime newSendAt) {
    sendAt = newSendAt;
  }

  public void markAsSent() {
    isSent = true;
  }

  public List<EmailReceiver> filterReceiversInPrevSet(Set<EmailReceiver> set) {
    return this.getEmailReceivers().getReceivers()
        .stream()
        .filter(set::contains)
        .toList();
  }

  public List<EmailReceiver> filterReceiversNotInPrevSet(Set<EmailReceiver> set) {
    return set
        .stream()
        .filter((e) -> !this.emailReceivers.getReceivers().contains(e))
        .toList();
  }
}
