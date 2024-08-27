package gdsc.konkuk.platformcore.fixture.attendance;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AttendanceFixture {
  private final Attendance fixture;

  @Builder
  public AttendanceFixture(Long id, Long eventId, String activeQrUuid) {
    this.fixture = Attendance.builder()
      .id(getDefault(id, 0L))
      .eventId(getDefault(eventId, 0L))
      .activeQrUuid(getDefault(activeQrUuid, "uuid"))
      .build();
  }
}
