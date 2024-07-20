package gdsc.konkuk.platformcore.application.event;

import gdsc.konkuk.platformcore.domain.event.entity.Event;

import java.time.LocalDateTime;

public record EventBrief(
    Long id, String title, String thumbnailUrl, LocalDateTime startAt, boolean hasAttendance) {

  public static EventBrief from(Event event) {
    return new EventBrief(
        event.getId(),
        event.getTitle(),
        event.getThumbnailUrl(),
        event.getStartAt(),
        event.isHasAttendance());
  }
}
