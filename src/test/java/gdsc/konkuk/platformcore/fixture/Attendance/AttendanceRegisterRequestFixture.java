package gdsc.konkuk.platformcore.fixture.Attendance;

import static gdsc.konkuk.platformcore.fixture.Attendance.AttendanceFixture.ATTENDANCE_ACTIVE_ID;

import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceRegisterRequest;
import gdsc.konkuk.platformcore.fixture.member.MemberFixture;

public class AttendanceRegisterRequestFixture {
  public static AttendanceRegisterRequest getActiveAttendanceRegisterRequestFixture() {
    return AttendanceRegisterRequest.builder()
      .eventId(ATTENDANCE_ACTIVE_ID)
      .batch(MemberFixture.BATCH)
      .build();
  }
}
