package gdsc.konkuk.platformcore.fixture.member;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceInfo;
import gdsc.konkuk.platformcore.application.member.dtos.MemberAttendances;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberAttendancesFixture {
  private final MemberAttendances fixture;

  @Builder
  public MemberAttendancesFixture(Long memberId, String memberName, MemberRole memberRole, String department, Long totalAttendances, Long actualAttendances, List<MemberAttendanceInfo> attendanceInfoList) {
    this.fixture = MemberAttendances.builder()
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
            .eventId(0L)
            .participantId(0L)
            .attendanceDate(LocalDateTime.now())
            .isAttended(true)
            .build(),
        MemberAttendanceInfo.builder()
            .attendanceId(1L)
            .memberId(0L)
            .eventId(1L)
            .participantId(1L)
            .attendanceDate(LocalDateTime.now().plusDays(3))
            .isAttended(false)
            .build(),
        MemberAttendanceInfo.builder()
            .attendanceId(2L)
            .memberId(0L)
            .eventId(2L)
            .participantId(2L)
            .attendanceDate(LocalDateTime.now().plusDays(5))
            .isAttended(true)
            .build())))
      .build();
  }
}