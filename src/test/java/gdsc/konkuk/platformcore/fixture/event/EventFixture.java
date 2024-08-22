package gdsc.konkuk.platformcore.fixture.event;

import static java.lang.Integer.parseInt;

import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.domain.event.entity.EventImage;
import java.time.LocalDateTime;
import java.util.List;

public class EventFixture {
  public static final String EVENT_YEAR = "2024";
  public static final String EVENT_MONTH = "07";

  public static final String EVENT_LOCATION = "Konkuk University";
  public static final String EVENT_CONTENT = "대충 즐겁게 놀았습니다~~";
  public static final List<EventImage> EVENT_IMAGE_LIST = List.of();
  public static final String EVENT_RETROSPECT_CONTENT = "대충 다음엔 더 잘해봅시다~~";

  public static final Long EVENT_1_ID = 1L;
  public static final String EVENT_1_TITLE = "Fixture Event 1";
  public static final LocalDateTime EVENT_1_START_AT = LocalDateTime.of(
      parseInt(EVENT_YEAR), parseInt(EVENT_MONTH), 3, 12, 0);
  public static final LocalDateTime EVENT_1_END_AT = LocalDateTime.of(
      parseInt(EVENT_YEAR), parseInt(EVENT_MONTH), 3, 15, 0);

  public static final Long EVENT_2_ID = 2L;
  public static final String EVENT_2_TITLE = "Fixture Event 2";
  public static final LocalDateTime EVENT_2_START_AT = LocalDateTime.of(
      parseInt(EVENT_YEAR), parseInt(EVENT_MONTH), 5, 12, 0);
  public static final LocalDateTime EVENT_2_END_AT = LocalDateTime.of(
      parseInt(EVENT_YEAR), parseInt(EVENT_MONTH), 5, 15, 0);

  public static final Long EVENT_3_ID = 3L;
  public static final String EVENT_3_TITLE = "Fixture Event 3";
  public static final LocalDateTime EVENT_3_START_AT = LocalDateTime.of(
      parseInt(EVENT_YEAR), parseInt(EVENT_MONTH), 8, 12, 0);
  public static final LocalDateTime EVENT_3_END_AT = LocalDateTime.of(
      parseInt(EVENT_YEAR), parseInt(EVENT_MONTH), 8, 15, 0);

  public static Event getEventFixture1() {
    return Event.builder()
      .id(EVENT_1_ID)
      .title(EVENT_1_TITLE)
      .content(EVENT_CONTENT)
      .location(EVENT_LOCATION)
      .startAt(EVENT_1_START_AT)
      .endAt(EVENT_1_END_AT)
      .eventImageList(EVENT_IMAGE_LIST)
      .retrospectContent(EVENT_RETROSPECT_CONTENT)
      .build();
  }

  public static Event getEventFixture2() {
    return Event.builder()
      .id(EVENT_2_ID)
      .title(EVENT_2_TITLE)
      .content(EVENT_CONTENT)
      .location(EVENT_LOCATION)
      .startAt(EVENT_2_START_AT)
      .endAt(EVENT_2_END_AT)
      .eventImageList(EVENT_IMAGE_LIST)
      .retrospectContent(EVENT_RETROSPECT_CONTENT)
      .build();
  }

  public static Event getEventFixture3() {
    return Event.builder()
      .id(EVENT_3_ID)
      .title(EVENT_3_TITLE)
      .content(EVENT_CONTENT)
      .location(EVENT_LOCATION)
      .startAt(EVENT_3_START_AT)
      .endAt(EVENT_3_END_AT)
      .eventImageList(EVENT_IMAGE_LIST)
      .retrospectContent(EVENT_RETROSPECT_CONTENT)
      .build();
  }
}
