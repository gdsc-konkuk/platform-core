package gdsc.konkuk.platformcore.fixture.event;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.controller.event.dtos.EventUpdateRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EventUpdateRequestFixture {
  private final EventUpdateRequest fixture;

  @Builder
  public EventUpdateRequestFixture(String title, String content, String location, LocalDateTime startAt, LocalDateTime endAt, List<URL> eventImagesToDelete)
      throws MalformedURLException {
    this.fixture = EventUpdateRequest.builder()
        .title(getDefault(title, "title"))
        .content(getDefault(content, "content"))
        .location(getDefault(location, "location"))
        .startAt(getDefault(startAt, LocalDateTime.now()))
        .endAt(getDefault(endAt, LocalDateTime.now().plusHours(1)))
        .eventImagesToDelete(getDefault(eventImagesToDelete, List.of(
            EventImageFixture.builder().url(new URL("https://s3.com/foo/bar1.png")).build().getFixture().getUrl(),
            EventImageFixture.builder().url(new URL("https://s3.com/foo/bar2.jpeg")).build().getFixture().getUrl()
        )))
        .build();
  }
}
