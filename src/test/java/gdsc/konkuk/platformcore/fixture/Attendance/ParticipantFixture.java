package gdsc.konkuk.platformcore.fixture.Attendance;

import static gdsc.konkuk.platformcore.fixture.Attendance.AttendanceFixture.getAttendanceFixtureActive;

import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import gdsc.konkuk.platformcore.fixture.member.MemberFixture;

public class ParticipantFixture {
  public static Participant getParticipantFixtureAttend() {
    return Participant.builder()
      .memberId(MemberFixture.GENERAL_1_ID)
      .attendance(getAttendanceFixtureActive())
      .isAttended(true)
      .build();
  }

  public static Participant getParticipantFixtureAbsent() {
    return Participant.builder()
      .memberId(MemberFixture.GENERAL_2_ID)
      .attendance(getAttendanceFixtureActive())
      .isAttended(false)
      .build();
  }
}
