package gdsc.konkuk.platformcore.application.event.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventWithAttendance {
  @NotNull private Long eventId;
  private Long attendanceId;
  @NotEmpty private String title;
  @NotNull private LocalDateTime startAt;
}
