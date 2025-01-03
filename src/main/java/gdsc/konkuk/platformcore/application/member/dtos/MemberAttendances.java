package gdsc.konkuk.platformcore.application.member.dtos;

import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceInfo;
import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceQueryDto;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MemberAttendances {

    @NotNull
    List<MemberAttendanceInfo> attendanceInfoList;
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

    public static List<MemberAttendances> from(List<MemberAttendanceQueryDto> list) {
        Map<Long, MemberAttendances> memberAttendanceInfoMap = new HashMap<>();
        for (MemberAttendanceQueryDto attendanceInfo : list) {
            MemberAttendances memberAttendances = memberAttendanceInfoMap
                    .computeIfAbsent(attendanceInfo.getMemberId(),
                            key -> createMemberAttendances(attendanceInfo));
            memberAttendances.attendanceInfoList.add(createMemberAttendanceInfo(attendanceInfo));
            updateAttendanceCounts(memberAttendances, attendanceInfo);
        }
        return new ArrayList<>(memberAttendanceInfoMap.values());
    }

    private static void updateAttendanceCounts(MemberAttendances memberAttendances,
            MemberAttendanceQueryDto attendanceInfo) {
        // TODO : 너무 높아진 복잡도, 리팩터링 필요
        memberAttendances.totalAttendances++;
        if (!attendanceInfo.getAttendanceType().isAbsent()) {
            memberAttendances.actualAttendances++;
        }
    }

    private static MemberAttendances createMemberAttendances(
            MemberAttendanceQueryDto attendanceInfo) {
        return MemberAttendances.builder()
                .memberId(attendanceInfo.getMemberId())
                .memberRole(attendanceInfo.getMemberRole())
                .memberName(attendanceInfo.getMemberName())
                .department(attendanceInfo.getMemberDepartment())
                .attendanceInfoList(new ArrayList<>())
                .totalAttendances(0L)
                .actualAttendances(0L)
                .build();
    }

    private static MemberAttendanceInfo createMemberAttendanceInfo(
            MemberAttendanceQueryDto attendanceInfo) {
        return MemberAttendanceInfo.builder()
                .memberId(attendanceInfo.getMemberId())
                .attendanceDate(attendanceInfo.getAttendanceDate())
                .participantId(attendanceInfo.getParticipantId())
                .attendanceId(attendanceInfo.getAttendanceId())
                .attendanceType(attendanceInfo.getAttendanceType())
                .build();
    }
}
