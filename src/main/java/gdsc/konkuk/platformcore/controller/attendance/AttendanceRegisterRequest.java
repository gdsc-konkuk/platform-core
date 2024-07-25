package gdsc.konkuk.platformcore.controller.attendance;

import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttendanceRegisterRequest {
  @NotNull
  private Long eventId;
  @NotEmpty
  private String batch;

  public static Attendance toEntity(AttendanceRegisterRequest request) {
    return Attendance.builder().eventId(request.getEventId()).build();
  }
}
