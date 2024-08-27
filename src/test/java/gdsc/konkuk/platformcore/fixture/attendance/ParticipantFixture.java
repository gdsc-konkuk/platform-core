package gdsc.konkuk.platformcore.fixture.attendance;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ParticipantFixture {
  private final Participant fixture;

  @Builder
  public ParticipantFixture(Long memberId, Attendance attendance, Boolean isAttended) {
    this.fixture = Participant.builder()
      .memberId(getDefault(memberId, 0L))
      .attendance(getDefault(attendance, AttendanceFixture.builder().build().getFixture()))
      .isAttended(getDefault(isAttended, false))
      .build();
  }
}
