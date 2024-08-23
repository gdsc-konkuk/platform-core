package gdsc.konkuk.platformcore.fixture.event;

import static gdsc.konkuk.platformcore.fixture.event.EventFixture.*;
import static gdsc.konkuk.platformcore.fixture.event.EventImageFixture.*;

import gdsc.konkuk.platformcore.controller.event.dtos.EventUpdateRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class EventUpdateRequestFixture {
  public static EventUpdateRequest getEventFixture1UpdateRequest() throws MalformedURLException {
    return EventUpdateRequest.builder()
        .title("updated title")
        .content("updated content")
        .location("updated location")
        .startAt(EVENT_2_START_AT)
        .endAt(EVENT_2_END_AT)
        .eventImagesToDelete(List.of(new URL(EVENT_1_IMAGE_URL_1), new URL(EVENT_1_IMAGE_URL_2)))
        .build();
  }
}
