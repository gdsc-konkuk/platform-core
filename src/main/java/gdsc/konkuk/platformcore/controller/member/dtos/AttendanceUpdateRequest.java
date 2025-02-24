package gdsc.konkuk.platformcore.controller.member.dtos;

import gdsc.konkuk.platformcore.application.member.dtos.AttendanceUpdateCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceUpdateRequest {

    @NotNull
    @Valid
    private List<AttendanceUpdateCommand> attendanceUpdateInfoList;
}
