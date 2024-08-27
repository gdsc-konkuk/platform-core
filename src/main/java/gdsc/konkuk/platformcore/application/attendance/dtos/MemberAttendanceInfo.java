package gdsc.konkuk.platformcore.application.attendance.dtos;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class MemberAttendanceInfo {
  private Long attendanceId;
  private Long eventId;
  private Long memberId;
  private boolean isAttended;
  private LocalDateTime attendanceDate;
  private Long participantId;
}
