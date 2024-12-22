package gdsc.konkuk.platformcore.controller.attendance.dtos;

import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import java.net.URI;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttendanceResponse {

    private Long attendanceId;
    private URI attendUrl;

    public static AttendanceResponse from(Attendance attendance, URI attendUrl) {
        return AttendanceResponse.builder()
                .attendanceId(attendance.getId())
                .attendUrl(attendUrl)
                .build();
    }
}
