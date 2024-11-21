package gdsc.konkuk.platformcore.controller.attendance.dtos;

import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttendanceInfo {
  private Long attendanceId;
  private String title;
  private LocalDateTime attendanceTime;

  public static AttendanceInfo from(Attendance attendance) {
       return AttendanceInfo.builder()
           .attendanceId(attendance.getId())
           .title(attendance.getTitle())
           .attendanceTime(attendance.getAttendanceTime())
           .build();
  }
}
