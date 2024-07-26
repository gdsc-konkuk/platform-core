package gdsc.konkuk.platformcore.controller.event;

import gdsc.konkuk.platformcore.domain.event.entity.Event;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventRegisterRequest {
  @NotEmpty private String title;
  private String description;
  private String thumbnailUrl;
  // TODO: event images
  @NotNull private LocalDateTime startAt;
  @NotNull private LocalDateTime endAt;

  public static Event toEntity(EventRegisterRequest request) {
    return Event.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .thumbnailUrl(request.getThumbnailUrl())
        .startAt(request.getStartAt())
        .endAt(request.getEndAt())
        .build();
  }
}
