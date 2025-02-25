package gdsc.konkuk.platformcore.util.fixture.member;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceInfo;
import gdsc.konkuk.platformcore.application.member.dtos.MemberAttendanceAggregate;
import gdsc.konkuk.platformcore.domain.attendance.entity.AttendanceType;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberAttendancesFixture {

    private final MemberAttendanceAggregate fixture;

    @Builder
    public MemberAttendancesFixture(Long memberId, String memberName, MemberRole memberRole,
            String department, Long totalAttendances, Long actualAttendances,
            List<MemberAttendanceInfo> attendanceInfoList) {
        this.fixture = MemberAttendanceAggregate.builder()
                .memberId(getDefault(memberId, 0L))
                .memberName(getDefault(memberName, "name"))
                .memberRole(getDefault(memberRole, MemberRole.MEMBER))
                .department(getDefault(department, "department"))
                .totalAttendances(getDefault(totalAttendances, 3L))
                .actualAttendances(getDefault(actualAttendances, 2L))
                .attendanceInfoList(getDefault(attendanceInfoList, List.of(
                        MemberAttendanceInfo.builder()
                                .attendanceId(0L)
                                .memberId(0L)
                                .participantId(0L)
                                .attendanceType(AttendanceType.ABSENT)
                                .attendanceDate(LocalDateTime.now())
                                .build(),
                        MemberAttendanceInfo.builder()
                                .attendanceId(1L)
                                .memberId(0L)
                                .participantId(1L)
                                .attendanceType(AttendanceType.LATE)
                                .attendanceDate(LocalDateTime.now().plusDays(3))
                                .build(),
                        MemberAttendanceInfo.builder()
                                .attendanceId(2L)
                                .memberId(0L)
                                .participantId(2L)
                                .attendanceType(AttendanceType.ATTEND)
                                .attendanceDate(LocalDateTime.now().plusDays(5))
                                .build())))
                .build();
    }
}
