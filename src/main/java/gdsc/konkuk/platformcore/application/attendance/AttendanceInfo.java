package gdsc.konkuk.platformcore.application.attendance;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AttendanceInfo {
  private Long attendanceId;
  private Long eventId;
  private Long memberId;
  private LocalDateTime attendanceDate;
  private boolean attendance;
}
