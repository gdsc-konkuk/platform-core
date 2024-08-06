package gdsc.konkuk.platformcore.application.event;

import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.external.s3.StorageClient;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
                    .thumbnail(getThumbnail(event.getEventImageKeys(), storageClient).orElse(null))
                    .build())
        .toList();
  }

  private static Optional<File> getThumbnail(
      List<String> eventImageKeys, StorageClient storageClient) {
    try {
      return Optional.of(storageClient.downloadFile(eventImageKeys.get(0)));
    } catch (IOException e) {
      return Optional.empty();
    }
  }
}
