package gdsc.konkuk.platformcore.domain.event.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
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

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @Column(name = "start_at")
  private LocalDateTime startAt;

  @Column(name = "end_at")
  private LocalDateTime endAt;

  @OneToMany(mappedBy = "eventId", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EventImage> eventImageList;

  @Embedded private Retrospect retrospect;

  @Builder
  public Event(
      Long id,
      String title,
      String content,
      LocalDateTime startAt,
      LocalDateTime endAt,
      List<EventImage> eventImageList,
      String retrospectContent) {
    this.id = id;
    this.title = validateNotNull(title, "title");
    this.content = validateNotNull(content, "content");
    this.startAt = validateNotNull(startAt, "startAt");
    this.endAt = validateNotNull(endAt, "endAt");
    this.eventImageList = eventImageList == null ? new ArrayList<>() : eventImageList;
    this.retrospect = Retrospect.builder().content(retrospectContent).build();
  }

  public List<String> getEventImageKeys() {
    return this.eventImageList.stream().map(EventImage::getObjectKey).toList();
  }

  public void addEventImageByKey(String imageKey) {
    EventImage eventImage = new EventImage(this.id, imageKey);
    this.eventImageList.add(eventImage);
  }

  public void deleteEventImageByKey(String imageKey) {
    this.eventImageList.removeIf(eventImage -> eventImage.isKeyEqual(imageKey));
  }

  public void updateRetrospectContent(String content) {
    this.retrospect.updateContent(content);
  }

  public void update(String title, String description, LocalDateTime startAt, LocalDateTime endAt) {
    if (title != null) this.title = title;
    if (description != null) this.content = description;
    if (startAt != null) this.startAt = startAt;
    if (endAt != null) this.endAt = endAt;
  }
}
