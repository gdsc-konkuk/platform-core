package gdsc.konkuk.platformcore.application.member.dtos;

import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceInfo;
import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceQueryDto;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MemberAttendanceAggregate {

    @NotNull
    private List<MemberAttendanceInfo> attendanceInfoList;

    @NotNull
    private Long memberId;

    @NotEmpty
    private String memberName;

    @NotNull
    private MemberRole memberRole;

    @NotEmpty
    private String department;

    @NotEmpty
    private Long totalAttendances;

    @NotEmpty
    private Long actualAttendances;

    public static MemberAttendanceAggregate from(
            MemberAttendanceQueryDto attendanceInfo) {
        return MemberAttendanceAggregate.builder()
                .memberId(attendanceInfo.getMemberId())
                .memberRole(attendanceInfo.getMemberRole())
                .memberName(attendanceInfo.getMemberName())
                .department(attendanceInfo.getMemberDepartment())
                .build();
    }
}
