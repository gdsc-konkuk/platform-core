package gdsc.konkuk.platformcore.domain.email.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import java.util.HashSet;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailReceivers {

  @BatchSize(size = 100)
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "email_receivers", joinColumns = @JoinColumn(name = "task_id"))
  Set<EmailReceiver> receivers = new HashSet<>();

  public EmailReceivers(Set<EmailReceiver> receivers) {
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

  public void removeAll() {
    this.receivers.clear();
  }

  public void insertAll(Set<EmailReceiver> set) {
    this.receivers.addAll(set);
  }
}
