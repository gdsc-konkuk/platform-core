package gdsc.konkuk.platformcore.domain.event.entity;

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

import java.time.LocalDateTime;

import static gdsc.konkuk.platformcore.global.utils.FieldValidator.validateNotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "thumbnail_url")
  private String thumbnailUrl;

  @Column(name = "start_at")
  private LocalDateTime startAt;

  @Column(name = "end_at")
  private LocalDateTime endAt;

  // TODO: event images

  @Embedded private Retrospect retrospect;

  @Builder
  public Event(
      Long id,
      String title,
      String description,
      String thumbnailUrl,
      LocalDateTime startAt,
      LocalDateTime endAt,
      String retrospectContent) {
    this.id = id;
    this.title = validateNotNull(title, "title");
    this.description = description;
    this.thumbnailUrl = thumbnailUrl;
    this.startAt = startAt;
    this.endAt = endAt;
    this.retrospect = Retrospect.builder().content(retrospectContent).build();
  }

  public void updateRetrospectContent(String content) {
    retrospect.updateContent(content);
  }
}
