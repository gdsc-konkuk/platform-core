package gdsc.konkuk.platformcore.domain.email.entity;

import static gdsc.konkuk.platformcore.global.utils.FieldValidator.*;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class EmailDetails {

  @Column(name = "email_subject")
  private String subject;
  @Column(name = "email_content", columnDefinition = "TEXT")
  private String content;

  public EmailDetails(String subject, String content) {
    this.subject = validateNotNull(subject, "subject");
    this.content = validateNotNull(content, "content");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    EmailDetails that = (EmailDetails)o;
    return Objects.equals(subject, that.subject) && Objects.equals(content, that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, content);
  }
}