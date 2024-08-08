package gdsc.konkuk.platformcore.application.event;

import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.external.s3.StorageClient;
import java.util.List;

public class EventMapper {

  public static List<EventBrief> mapEventListToEventBriefList(
      List<Event> events, StorageClient storageClient) {
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
