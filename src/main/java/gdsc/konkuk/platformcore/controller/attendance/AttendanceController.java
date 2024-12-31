package gdsc.konkuk.platformcore.controller.attendance;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.apiPath;
import static gdsc.konkuk.platformcore.global.consts.SPAConstants.SPA_ADMIN_ATTENDANCE_FAIL_REDIRECT_URL;
import static gdsc.konkuk.platformcore.global.consts.SPAConstants.SPA_ADMIN_ATTENDANCE_SUCCESS_REDIRECT_URL;
import static org.springframework.http.HttpStatusCode.valueOf;

import gdsc.konkuk.platformcore.application.attendance.AttendanceService;
import gdsc.konkuk.platformcore.application.attendance.dtos.AttendanceStatus;
import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceErrorCode;
import gdsc.konkuk.platformcore.application.attendance.exceptions.QrInvalidException;
import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceInfo;
import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceRegisterRequest;
import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceResponse;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import gdsc.konkuk.platformcore.global.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<SuccessResponse> getEventsOfTheMonthWithAttendance(
            @RequestParam Integer year, @RequestParam Integer month) {
        List<Attendance> attendances = attendanceService.getAllByPeriod(
                LocalDate.of(year, month, 1));
        List<AttendanceInfo> attendanceInfos = attendances.stream().map(AttendanceInfo::from)
                .toList();
        return ResponseEntity.ok(SuccessResponse.of(attendanceInfos));
    }

    @GetMapping("/attend/{attendanceId}")
    public ResponseEntity<?> attend(@PathVariable Long attendanceId, @RequestParam String qrUuid) {
        try {
            Long currentId = SecurityUtils.getCurrentUserId();
            attendanceService.attend(currentId, attendanceId, qrUuid);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", SPA_ADMIN_ATTENDANCE_SUCCESS_REDIRECT_URL);
            return new ResponseEntity<>(headers,
                    valueOf(HttpServletResponse.SC_TEMPORARY_REDIRECT));
        } catch (Exception e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", SPA_ADMIN_ATTENDANCE_FAIL_REDIRECT_URL);
            return new ResponseEntity<>(headers,
                    valueOf(HttpServletResponse.SC_TEMPORARY_REDIRECT));
        }
    }

    @PostMapping()
    public ResponseEntity<SuccessResponse> registerAttendance(
            @RequestBody AttendanceRegisterRequest registerRequest) {
        String title = registerRequest.getTitle();
        String batch = registerRequest.getBatch();
        Attendance attendance = attendanceService.registerAttendance(title, batch);
        AttendanceResponse response = AttendanceResponse.from(attendance,
                generateAttendUri(attendance));
        return ResponseEntity.created(generateAttendanceUri(attendance))
                .body(SuccessResponse.of(response));
    }

    // TODO: 추후 WebSocket으로 변경 필요
    @GetMapping("/{attendanceId}/status")
    public ResponseEntity<SuccessResponse> getAttendance(@PathVariable Long attendanceId) {
        AttendanceStatus attendanceStatus = attendanceService.getAttendanceStatus(attendanceId);
        return ResponseEntity.ok(SuccessResponse.of(attendanceStatus));
    }

    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<SuccessResponse> deleteAttendance(@PathVariable Long attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{attendanceId}/qr")
    public ResponseEntity<SuccessResponse> generateQr(@PathVariable Long attendanceId) {
        Attendance attendance = attendanceService.generateQr(attendanceId);
        AttendanceResponse response = AttendanceResponse.from(attendance,
                generateAttendUri(attendance));
        return ResponseEntity.created(generateQrUri(attendance))
                .body(SuccessResponse.of(response));
    }

    @DeleteMapping("/{attendanceId}/qr")
    public ResponseEntity<SuccessResponse> expireQr(
            @PathVariable Long attendanceId) {
        attendanceService.expireQr(attendanceId);
        return ResponseEntity.noContent().build();
    }

    private URI generateAttendanceUri(Attendance attendance) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{attendanceId}")
                .buildAndExpand(attendance.getId())
                .toUri();
    }

    private URI generateQrUri(Attendance attendance) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{attendanceId}/qr")
                .buildAndExpand(attendance.getId())
                .toUri();
    }

    private URI generateAttendUri(Attendance attendance) {
        if (attendance.getActiveQrUuid() == null) {
            throw QrInvalidException.of(AttendanceErrorCode.INVALID_QR_UUID);
        }

        return ServletUriComponentsBuilder.fromCurrentServletMapping()
                .path(apiPath("/attendances/attend/{attendanceId}"))
                .queryParam("qrUuid", attendance.getActiveQrUuid())
                .buildAndExpand(attendance.getId())
                .toUri();
    }
}
