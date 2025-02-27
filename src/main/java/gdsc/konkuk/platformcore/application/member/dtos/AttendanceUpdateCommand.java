package gdsc.konkuk.platformcore.application.member.dtos;

import gdsc.konkuk.platformcore.domain.attendance.entity.AttendanceType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttendanceUpdateCommand {

    @NotNull
    private Long participantId;

    @NotNull
    private AttendanceType attendanceType;
}
