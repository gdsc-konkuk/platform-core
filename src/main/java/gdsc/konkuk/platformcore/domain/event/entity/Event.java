package gdsc.konkuk.platformcore.domain.event.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

  @Column(name = "location")
  private String location;

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
      String location,
      LocalDateTime startAt,
      LocalDateTime endAt,
      List<EventImage> eventImageList,
      String retrospectContent) {
    this.id = id;
    this.title = validateNotNull(title, "title");
    this.content = validateNotNull(content, "content");
    this.location = validateNotNull(location, "location");
    this.startAt = validateNotNull(startAt, "startAt");
    this.endAt = validateNotNull(endAt, "endAt");
    this.eventImageList = eventImageList == null ? new ArrayList<>() : eventImageList;
    this.retrospect = Retrospect.builder().content(retrospectContent).build();
  }

  public Optional<URL> getThumbnail() {
    try {
      // Event의 첫 사진을 썸네일로 사용
      return Optional.of(this.eventImageList.get(0).getUrl());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public List<URL> getEventImageUrls() {
    return this.eventImageList.stream().map(EventImage::getUrl).toList();
  }

  public void addEventImageByUrl(URL imageUrl) {
    EventImage eventImage = new EventImage(this.id, imageUrl);
    this.eventImageList.add(eventImage);
  }

  public void deleteEventImageByUrl(URL imageUrl) {
    this.eventImageList.removeIf(eventImage -> eventImage.isUrlEqual(imageUrl));
  }

  public void updateRetrospectContent(String content) {
    this.retrospect.updateContent(content);
  }

  public void update(String title, String content, String location, LocalDateTime startAt, LocalDateTime endAt) {
    if (title != null) this.title = title;
    if (content != null) this.content = content;
    if (location != null) this.location = location;
    if (startAt != null) this.startAt = startAt;
    if (endAt != null) this.endAt = endAt;
  }
}
