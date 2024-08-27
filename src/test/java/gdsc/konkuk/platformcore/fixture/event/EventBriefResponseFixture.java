package gdsc.konkuk.platformcore.fixture.event;

import static gdsc.konkuk.platformcore.application.event.EventMapper.mapEventListToEventBriefList;
import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.application.event.dtos.EventBrief;
import gdsc.konkuk.platformcore.controller.event.dtos.EventBriefResponse;
import java.net.MalformedURLException;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EventBriefResponseFixture {
  private final EventBriefResponse fixture;

  @Builder
  public EventBriefResponseFixture(List<EventBrief> eventBriefs) throws MalformedURLException {
    this.fixture = EventBriefResponse.builder()
        .eventBriefs(getDefault(eventBriefs, mapEventListToEventBriefList(
            List.of(
                EventFixture.builder().id(1L).build().getFixture(),
                EventFixture.builder().id(2L).build().getFixture(),
                EventFixture.builder().id(3L).build().getFixture()
            ))))
        .build();
  }
}
