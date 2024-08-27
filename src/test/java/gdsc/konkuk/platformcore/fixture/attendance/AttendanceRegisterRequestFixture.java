package gdsc.konkuk.platformcore.fixture.attendance;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceRegisterRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AttendanceRegisterRequestFixture {
  private final AttendanceRegisterRequest fixture;

  @Builder
  public AttendanceRegisterRequestFixture(Long eventId, String batch) {
    this.fixture = AttendanceRegisterRequest.builder()
      .eventId(getDefault(eventId, 0L))
      .batch(getDefault(batch, "24-25"))
      .build();
  }
}
