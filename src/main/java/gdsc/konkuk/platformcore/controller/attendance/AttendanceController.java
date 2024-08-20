package gdsc.konkuk.platformcore.controller.attendance;

import gdsc.konkuk.platformcore.application.attendance.AttendanceService;
import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceErrorCode;
import gdsc.konkuk.platformcore.application.attendance.exceptions.QrInvalidException;
import gdsc.konkuk.platformcore.application.event.EventService;
import gdsc.konkuk.platformcore.application.event.dtos.EventWithAttendance;
import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendSuccessResponse;
import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceRegisterRequest;
import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceResponse;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

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
  public ResponseEntity<SuccessResponse> attend(
    @PathVariable Long attendanceId,
    @RequestParam String qrUuid,
    @AuthenticationPrincipal OidcUser oidcUser) {
    Participant participant = attendanceService.attend(oidcUser.getEmail(), attendanceId, qrUuid);
    return ResponseEntity.ok(SuccessResponse.of(AttendSuccessResponse.from(participant, attendanceId)));
  }

  @PostMapping()
  public ResponseEntity<SuccessResponse> registerAttendance(
    @RequestBody @Valid AttendanceRegisterRequest registerRequest) {
    Attendance attendance = attendanceService.registerAttendance(registerRequest);
    AttendanceResponse response = AttendanceResponse.from(attendance, generateAttendUri(attendance));
    return ResponseEntity.created(generateAttendanceUri(attendance))
      .body(SuccessResponse.of(response));
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

    return ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/attend/{attendanceId}")
      .queryParam("qrUuid", attendance.getActiveQrUuid())
      .buildAndExpand(attendance.getId())
      .toUri();
  }
}
