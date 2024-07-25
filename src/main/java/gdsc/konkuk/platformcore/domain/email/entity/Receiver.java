package gdsc.konkuk.platformcore.domain.email.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Receiver {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "receiver_id")
  private Long id;

  @Column(name = "receiver_dest")
  private String dest;

  @Column(name = "receiver_is_sent")
  private boolean isSent;

  @Builder
  public Receiver(String dest, boolean isSent) {
    this.dest = dest;
    this.isSent = isSent;
  }
}
