package gdsc.konkuk.platformcore.domain.email.entity;

import static gdsc.konkuk.platformcore.global.utils.FieldValidator.validateNotNull;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlatformEmail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "email_id")
  private Long id;

  @Column(name = "email_content", columnDefinition = "TEXT")
  private String content;

  private String subject;

  private boolean isHtml = true;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "email_id")
  List<Receiver> receivers = new ArrayList<>();

  @Builder
  public PlatformEmail(String content, String subject) {
    this.content = validateNotNull(content, "content");
    this.subject = validateNotNull(subject, "subject");
  }

  public void addReceivers(List<String> receiverList) {
    for (String receiverEmail : receiverList) {
      Receiver receiver = new Receiver(receiverEmail, false);
      receivers.add(receiver);
    }
  }
}
