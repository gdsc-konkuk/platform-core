package gdsc.konkuk.platformcore.controller.event;

import gdsc.konkuk.platformcore.external.s3.StorageObject;
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
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private List<StorageObject> images;

  @Builder
  public EventDetailResponse(
      Long id,
      String title,
      String content,
      LocalDateTime startAt,
      LocalDateTime endAt,
      List<StorageObject> images) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.startAt = startAt;
    this.endAt = endAt;
    this.images = images;
  }
}
