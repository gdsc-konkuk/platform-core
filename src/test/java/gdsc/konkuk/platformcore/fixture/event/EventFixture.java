package gdsc.konkuk.platformcore.fixture.event;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.domain.event.entity.EventImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EventFixture {
  private final Event fixture;

  @Builder
  public EventFixture(Long id, String title, String content, String location, LocalDateTime startAt, LocalDateTime endAt, List<EventImage> eventImageList, String retrospectContent)
      throws MalformedURLException {
    this.fixture = Event.builder()
      .id(getDefault(id, 0L))
      .title(getDefault(title, "title"))
      .content(getDefault(content, "content"))
      .location(getDefault(location, "location"))
      .startAt(getDefault(startAt, LocalDateTime.now()))
      .endAt(getDefault(endAt, LocalDateTime.now().plusHours(1)))
      .eventImageList(getDefault(eventImageList, List.of(
          EventImageFixture.builder().url(new URL("https://s3.com/foo/bar1.png")).build().getFixture(),
          EventImageFixture.builder().url(new URL("https://s3.com/foo/bar2.jpeg")).build().getFixture()
      )))
      .retrospectContent(getDefault(retrospectContent, "retrospect"))
      .build();
  }
}
