package gdsc.konkuk.platformcore.application.attendance.dtos;

import gdsc.konkuk.platformcore.domain.attendance.entity.AttendanceType;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MemberAttendanceQueryDto {

    private Long memberId;
    private String memberName;
    private MemberRole memberRole;
    private String memberDepartment;
    private Long participantId;
    private Long attendanceId;
    private LocalDateTime attendanceDate;
    private AttendanceType attendanceType;
}
