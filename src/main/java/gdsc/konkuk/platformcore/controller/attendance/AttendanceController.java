package gdsc.konkuk.platformcore.controller.attendance;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.apiPath;
import static org.springframework.http.HttpStatusCode.valueOf;

import gdsc.konkuk.platformcore.application.attendance.AttendanceService;
import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceErrorCode;
import gdsc.konkuk.platformcore.application.attendance.exceptions.QrInvalidException;
import gdsc.konkuk.platformcore.application.event.EventService;
import gdsc.konkuk.platformcore.application.event.dtos.EventWithAttendance;
import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceRegisterRequest;
import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceResponse;
import gdsc.konkuk.platformcore.application.attendance.dtos.AttendanceStatus;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import software.amazon.awssdk.http.HttpStatusCode;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {
  private final AttendanceService attendanceService;
  private final EventService eventService;

  @GetMapping
  public ResponseEntity<SuccessResponse> getEventsOfTheMonthWithAttendance(
      @RequestParam Integer year, @RequestParam Integer month) {
    List<EventWithAttendance> eventsOfMonthWithAttendance =
        eventService.getEventsOfTheMonthWithAttendance(LocalDate.of(year, month, 1));
    return ResponseEntity.ok(SuccessResponse.of(eventsOfMonthWithAttendance));
  }

  @GetMapping("/attend/{attendanceId}")
  public ResponseEntity<?> attend(
    @PathVariable Long attendanceId,
    @RequestParam String qrUuid,
    @AuthenticationPrincipal OidcUser oidcUser) {
    try{
      attendanceService.attend(oidcUser.getEmail(), attendanceId, qrUuid);
      HttpHeaders headers = new HttpHeaders();
      headers.add("Location", "/admin/success");
      return new ResponseEntity<>(headers, valueOf(HttpStatusCode.TEMPORARY_REDIRECT));
    }catch(Exception e) {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Location", "/admin/fail");
      return new ResponseEntity<>(headers, valueOf(HttpStatusCode.TEMPORARY_REDIRECT));
    }
  }

  @PostMapping()
  public ResponseEntity<SuccessResponse> registerAttendance(
    @RequestBody @Valid AttendanceRegisterRequest registerRequest) {
    Attendance attendance = attendanceService.registerAttendance(registerRequest);
    AttendanceResponse response = AttendanceResponse.from(attendance, generateAttendUri(attendance));
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
    AttendanceResponse response = AttendanceResponse.from(attendance, generateAttendUri(attendance));
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
    if(attendance.getActiveQrUuid() == null) {
      throw QrInvalidException.of(AttendanceErrorCode.INVALID_QR_UUID);
    }

    return ServletUriComponentsBuilder.fromCurrentServletMapping()
      .path(apiPath("/attendances/attend/{attendanceId}"))
      .queryParam("qrUuid", attendance.getActiveQrUuid())
      .buildAndExpand(attendance.getId())
      .toUri();
  }
}
