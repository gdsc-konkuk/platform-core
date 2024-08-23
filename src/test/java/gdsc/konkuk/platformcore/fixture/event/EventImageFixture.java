package gdsc.konkuk.platformcore.fixture.event;

import static gdsc.konkuk.platformcore.fixture.event.EventFixture.EVENT_1_ID;

import gdsc.konkuk.platformcore.domain.event.entity.EventImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class EventImageFixture {
  public static final String EVENT_1_IMAGE_URL_1 = "https://example.com/image1.jpg";
  public static final String EVENT_1_IMAGE_URL_2 = "https://example.com/image2.png";
  public static final String EVENT_1_IMAGE_URL_3 = "https://example.com/image3.jpeg";

  public static List<EventImage> getEvent1ImageList() {
    try {
      return List.of(
        getEventImageFixture1(),
        getEventImageFixture2(),
        getEventImageFixture3());
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private static EventImage getEventImageFixture1() throws MalformedURLException {
    return EventImage.builder()
      .eventId(EVENT_1_ID)
      .url(new URL(EVENT_1_IMAGE_URL_1))
      .build();
  }

  private static EventImage getEventImageFixture2() throws MalformedURLException {
    return EventImage.builder()
      .eventId(EVENT_1_ID)
      .url(new URL(EVENT_1_IMAGE_URL_2))
      .build();
  }

  private static EventImage getEventImageFixture3() throws MalformedURLException {
    return EventImage.builder()
      .eventId(EVENT_1_ID)
      .url(new URL(EVENT_1_IMAGE_URL_3))
      .build();
  }
}
