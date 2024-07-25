package gdsc.konkuk.platformcore.domain.email.entity;

import static gdsc.konkuk.platformcore.global.utils.FieldValidator.validateNotNull;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "email_id")
  private Long id;

  @Lob
  @Column(name = "email_content")
  private String content;

  private String subject;

  private boolean isHtml = true;

  @OneToMany(mappedBy = "email", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<Receiver> receivers = new ArrayList<>();

  @Builder
  public Email(String content, String subject) {
    this.content = validateNotNull(content, "content");
    this.subject = validateNotNull(subject, "content");
  }

  public void addReceivers(List<String> receiverList) {
    for (String receiverEmail : receiverList) {
      Receiver receiver = new Receiver(receiverEmail, false);
      receiver.registerEmail(this);
      receivers.add(receiver);
    }
  }
}
