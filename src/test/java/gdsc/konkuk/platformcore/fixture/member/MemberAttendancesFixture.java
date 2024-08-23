package gdsc.konkuk.platformcore.fixture.member;

import static gdsc.konkuk.platformcore.fixture.Attendance.AttendanceFixture.*;
import static gdsc.konkuk.platformcore.fixture.event.EventFixture.*;
import static gdsc.konkuk.platformcore.fixture.member.MemberFixture.*;

import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceInfo;
import gdsc.konkuk.platformcore.application.member.dtos.MemberAttendances;
import java.util.List;

public class MemberAttendancesFixture {
  public static MemberAttendances getGeneralMember1AttendanceFixture(){
    return MemberAttendances.builder()
      .memberId(GENERAL_1_ID)
      .memberName(GENERAL_1_NAME)
      .memberRole(GENERAL_ROLE)
      .department(GENERAL_1_DEPARTMENT)
      .totalAttendances(TOTAL_ATTENDANCE_COUNT)
      .actualAttendances(ACTUAL_ATTENDANCE_COUNT)
      .attendanceInfoList(generateMemberAttendanceInfos(GENERAL_1_ID, 0L))
      .build();
  }

  public static MemberAttendances getGeneralMember2AttendanceFixture(){
  return MemberAttendances.builder()
    .memberId(GENERAL_2_ID)
    .memberName(GENERAL_2_NAME)
    .memberRole(GENERAL_ROLE)
    .department(GENERAL_2_DEPARTMENT)
    .totalAttendances(TOTAL_ATTENDANCE_COUNT)
    .actualAttendances(ACTUAL_ATTENDANCE_COUNT)
    .attendanceInfoList(generateMemberAttendanceInfos(GENERAL_2_ID, TOTAL_ATTENDANCE_COUNT))
    .build();
  }

  public static MemberAttendances getGeneralMember3AttendanceFixture(){
  return MemberAttendances.builder()
    .memberId(GENERAL_3_ID)
    .memberName(GENERAL_3_NAME)
    .memberRole(GENERAL_ROLE)
    .department(GENERAL_3_DEPARTMENT)
    .totalAttendances(TOTAL_ATTENDANCE_COUNT)
    .actualAttendances(ACTUAL_ATTENDANCE_COUNT)
    .attendanceInfoList(generateMemberAttendanceInfos(GENERAL_3_ID, TOTAL_ATTENDANCE_COUNT * 2))
    .build();
  }

  private static final Long TOTAL_ATTENDANCE_COUNT = 3L;
  private static final Long ACTUAL_ATTENDANCE_COUNT = 2L;

  private static List<MemberAttendanceInfo> generateMemberAttendanceInfos(Long memberId, Long participantIdOffset){
    return List.of(
      MemberAttendanceInfo.builder()
          .attendanceId(ATTENDANCE_ACTIVE_ID)
          .memberId(memberId)
          .eventId(EVENT_1_ID)
          .participantId(participantIdOffset + 1L)
          .attendanceDate(EVENT_1_START_AT)
          .isAttended(true)
          .build(),
      MemberAttendanceInfo.builder()
          .attendanceId(ATTENDANCE_INACTIVE_ID)
          .memberId(memberId)
          .eventId(EVENT_2_ID)
          .participantId(participantIdOffset + 2L)
          .attendanceDate(EVENT_2_START_AT)
          .isAttended(false)
          .build(),
      MemberAttendanceInfo.builder()
          .attendanceId(ATTENDANCE_EMPTY_ID)
          .memberId(memberId)
          .eventId(EVENT_3_ID)
          .participantId(participantIdOffset + 3L)
          .attendanceDate(EVENT_3_START_AT)
          .isAttended(true)
          .build());
  }
}
