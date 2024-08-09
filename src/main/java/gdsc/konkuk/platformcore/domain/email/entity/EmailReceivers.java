package gdsc.konkuk.platformcore.domain.email.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailReceivers {

  @ElementCollection
  @CollectionTable(
      name = "email_receivers",
      joinColumns = @JoinColumn(name = "task_id")
  )
  @Column(name = "receiver_email")
  List<String> receivers = new ArrayList<>();

  public EmailReceivers(List<String> receivers) {
    this.receivers.addAll(receivers);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EmailReceivers that = (EmailReceivers) o;
    return Objects.equals(receivers, that.receivers);
  }
}
