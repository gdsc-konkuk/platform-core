package gdsc.konkuk.platformcore.fixture.event;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.controller.event.dtos.EventRegisterRequest;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EventRegisterRequestFixture {
  private final EventRegisterRequest fixture;

  @Builder
  public EventRegisterRequestFixture(String title, String content, String location, LocalDateTime startAt, LocalDateTime endAt) {
    this.fixture = EventRegisterRequest.builder()
        .title(getDefault(title, "title"))
        .content(getDefault(content, "content"))
        .location(getDefault(location, "location"))
        .startAt(getDefault(startAt, LocalDateTime.now()))
        .endAt(getDefault(endAt, LocalDateTime.now().plusHours(1)))
        .build();
  }
}
