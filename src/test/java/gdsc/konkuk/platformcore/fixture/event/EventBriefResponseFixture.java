package gdsc.konkuk.platformcore.fixture.event;

import static gdsc.konkuk.platformcore.application.event.EventMapper.mapEventListToEventBriefList;
import static gdsc.konkuk.platformcore.fixture.event.EventFixture.*;

import gdsc.konkuk.platformcore.controller.event.dtos.EventBriefResponse;
import java.util.List;

public class EventBriefResponseFixture {
  public static EventBriefResponse getEventBriefResponseFixture(){
    return EventBriefResponse.builder()
        .eventBriefs(
            mapEventListToEventBriefList(List.of(getEventFixture1(), getEventFixture2(), getEventFixture3())))
        .build();
  }
}
