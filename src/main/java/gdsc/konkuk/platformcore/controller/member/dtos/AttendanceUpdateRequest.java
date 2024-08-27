package gdsc.konkuk.platformcore.controller.member.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceUpdateRequest {
  @NotNull @Valid private List<AttendanceUpdateInfo> attendanceUpdateInfoList;
}
