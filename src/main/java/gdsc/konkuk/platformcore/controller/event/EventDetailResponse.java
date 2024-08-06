package gdsc.konkuk.platformcore.controller.event;

import java.io.File;
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
  private List<File> images;

  @Builder
  public EventDetailResponse(
      Long id,
      String title,
      String content,
      LocalDateTime startAt,
      LocalDateTime endAt,
      List<File> images) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.startAt = startAt;
    this.endAt = endAt;
    this.images = images;
  }
}
