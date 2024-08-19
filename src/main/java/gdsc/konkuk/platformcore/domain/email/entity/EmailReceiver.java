package gdsc.konkuk.platformcore.domain.email.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailReceiver {
  @Column(name = "receiver_email")
  private String email;

  @Column(name = "receiver_name")
  private String name;

  @Builder
  public EmailReceiver(String email, String name) {
    this.email = email;
    this.name = name;
  }
}
