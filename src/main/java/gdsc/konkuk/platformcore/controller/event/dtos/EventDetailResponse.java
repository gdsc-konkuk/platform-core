package gdsc.konkuk.platformcore.controller.event.dtos;

import gdsc.konkuk.platformcore.domain.event.entity.Event;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EventDetailResponse {
  private Long id;
  private String title;
  private String content;
  private String location;
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private List<URL> images;
  private String retrospect;

  @Builder
  public EventDetailResponse(
      Long id,
      String title,
      String content,
      String location,
      LocalDateTime startAt,
      LocalDateTime endAt,
      List<URL> images,
      String retrospect) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.location = location;
    this.startAt = startAt;
    this.endAt = endAt;
    this.images = images;
    this.retrospect = retrospect;
  }

  public static EventDetailResponse fromEntity(Event event) {
    return EventDetailResponse.builder()
        .id(event.getId())
        .title(event.getTitle())
        .content(event.getContent())
        .location(event.getLocation())
        .startAt(event.getStartAt())
        .endAt(event.getEndAt())
        .images(event.getEventImageUrls())
        .retrospect(event.getRetrospect().getContent())
        .build();
  }
}
