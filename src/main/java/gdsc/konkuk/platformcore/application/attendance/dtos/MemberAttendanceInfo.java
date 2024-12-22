package gdsc.konkuk.platformcore.application.attendance.dtos;

import gdsc.konkuk.platformcore.domain.attendance.entity.AttendanceType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class MemberAttendanceInfo {

    private Long attendanceId;
    private Long memberId;
    private AttendanceType attendanceType;
    private LocalDateTime attendanceDate;
    private Long participantId;
}
