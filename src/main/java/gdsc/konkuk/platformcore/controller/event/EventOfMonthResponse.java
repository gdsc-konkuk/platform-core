package gdsc.konkuk.platformcore.controller.event;

import gdsc.konkuk.platformcore.domain.event.entity.Event;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventOfMonthResponse {
  @NotEmpty private Long id;
  @NotEmpty private String title;
  @NotEmpty private String thumbnailUrl;
  @NotEmpty private LocalDateTime startAt;
  @NotEmpty private boolean hasAttendance;

  public static EventOfMonthResponse from(Event event) {
    return new EventOfMonthResponse(
        event.getId(),
        event.getTitle(),
        event.getThumbnailUrl(),
        event.getStartAt(),
        event.isHasAttendance());
  }
}
