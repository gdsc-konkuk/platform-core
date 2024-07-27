package gdsc.konkuk.platformcore.domain.retrospect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static gdsc.konkuk.platformcore.global.utils.FieldValidator.validateNotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Retrospect {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_id")
  private Long eventId;

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @Builder
  public Retrospect(Long eventId, String content) {
    this.eventId = validateNotNull(eventId, "eventId");
    this.content = validateNotNull(content, "content");
  }

  public void updateContent(String content) {
    this.content = validateNotNull(content, "content");
  }
}
