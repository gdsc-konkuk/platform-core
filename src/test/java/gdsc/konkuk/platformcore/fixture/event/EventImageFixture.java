package gdsc.konkuk.platformcore.fixture.event;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.domain.event.entity.EventImage;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EventImageFixture {
  private final EventImage fixture;

  @Builder
  public EventImageFixture(Long eventId, URL url) throws MalformedURLException {
    this.fixture = EventImage.builder()
      .eventId(getDefault(eventId, 0L))
      .url(getDefault(url, new URL("https://example.com/foo/bar.jpg")))
      .build();
  }
}
