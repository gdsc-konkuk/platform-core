package gdsc.konkuk.platformcore.controller.attendance.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttendanceRegisterRequest {

    @NotEmpty
    private String title;
    @NotEmpty
    private String batch;
}
