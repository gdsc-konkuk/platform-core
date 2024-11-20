package gdsc.konkuk.platformcore.fixture.attendance;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AttendanceFixture {
  private final Attendance fixture;

  @Builder
  public AttendanceFixture(Long id, String title, LocalDateTime attendanceTime, String activeQrUuid) {
    this.fixture = Attendance.builder()
      .id(getDefault(id, 0L))
      .title(getDefault(title, "title"))
      .attendanceTime(getDefault(attendanceTime, LocalDateTime.now()))
      .activeQrUuid(getDefault(activeQrUuid, "uuid"))
      .build();
  }
}
