package gdsc.konkuk.platformcore.application.event;

import gdsc.konkuk.platformcore.application.event.dtos.EventBrief;
import gdsc.konkuk.platformcore.domain.event.entity.Event;
import java.util.List;

public class EventMapper {

  public static List<EventBrief> mapEventListToEventBriefList(
      List<Event> events) {
    return events.stream()
        .map(
            event ->
                EventBrief.builder()
                    .id(event.getId())
                    .title(event.getTitle())
                    .content(event.getContent())
                    .startAt(event.getStartAt())
                    .thumbnail(event.getThumbnail().orElse(null))
                    .build())
        .toList();
  }
}
