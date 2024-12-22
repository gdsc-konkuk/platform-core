package gdsc.konkuk.platformcore.application.attendance.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttendanceStatus {

    private Long attendanceId;
    private int total;
    private int attended;

    public static AttendanceStatus of(Long attendanceId, int total, int attended) {
        return AttendanceStatus.builder()
                .attendanceId(attendanceId)
                .total(total)
                .attended(attended)
                .build();
    }
}
