package gdsc.konkuk.platformcore.fixture.event;

import static gdsc.konkuk.platformcore.fixture.event.EventFixture.*;

import gdsc.konkuk.platformcore.controller.event.dtos.EventRegisterRequest;

public class EventRegisterRequestFixture {
  public static EventRegisterRequest getEventFixture1RegisterRequest(){
    return EventRegisterRequest.builder()
        .title(EVENT_1_TITLE)
        .content(EVENT_CONTENT)
        .location(EVENT_LOCATION)
        .startAt(EVENT_1_START_AT)
        .endAt(EVENT_1_END_AT)
        .build();
  }
}
