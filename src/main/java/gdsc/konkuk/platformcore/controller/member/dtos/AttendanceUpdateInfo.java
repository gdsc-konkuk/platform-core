package gdsc.konkuk.platformcore.controller.member.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttendanceUpdateInfo {
  @NotNull private Long participantId;
  @NotNull private boolean isAttended;
}
