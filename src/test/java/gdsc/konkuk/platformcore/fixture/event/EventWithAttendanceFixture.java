package gdsc.konkuk.platformcore.fixture.event;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.application.event.dtos.EventWithAttendance;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EventWithAttendanceFixture {
  private final EventWithAttendance fixture;

  @Builder
  public EventWithAttendanceFixture(Long eventId, Long attendanceId, String title, LocalDateTime startAt) {
    this.fixture = EventWithAttendance.builder()
      .eventId(getDefault(eventId, 0L))
      .attendanceId(getDefault(attendanceId, null))
      .title(getDefault(title, "title"))
      .startAt(getDefault(startAt, LocalDateTime.now()))
      .build();
  }
}
