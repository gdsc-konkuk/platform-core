package gdsc.konkuk.platformcore.application.attendance;

import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceErrorCode;
import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceNotFoundException;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import gdsc.konkuk.platformcore.domain.attendance.repository.AttendanceRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AttendanceServiceHelper {

  public static Attendance findAttendanceById(
      AttendanceRepository attendanceRepository, Long attendanceId) {
    return attendanceRepository
        .findById(attendanceId)
        .orElseThrow(
            () -> AttendanceNotFoundException.of(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));
  }
}
