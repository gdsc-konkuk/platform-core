package gdsc.konkuk.platformcore.controller.attendance;

import gdsc.konkuk.platformcore.application.attendance.AttendanceService;
import gdsc.konkuk.platformcore.application.event.EventService;
import gdsc.konkuk.platformcore.application.event.EventWithAttendance;
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
    return ResponseEntity.ok(SuccessResponse.of(participant));
  }

  @PostMapping()
  public ResponseEntity<SuccessResponse> registerAttendance(
    @RequestBody @Valid AttendanceRegisterRequest registerRequest) {
    Long attendanceId = attendanceService.registerAttendance(registerRequest);
    return ResponseEntity.created(generateAttendanceUri(attendanceId))
      .body(SuccessResponse.messageOnly());
  }

  @DeleteMapping("/{attendanceId}")
  public ResponseEntity<SuccessResponse> deleteAttendance(@PathVariable Long attendanceId) {
    attendanceService.deleteAttendance(attendanceId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{attendanceId}/qr")
  public ResponseEntity<SuccessResponse> generateQr(@PathVariable Long attendanceId) {
    String qrUuid = attendanceService.generateQr(attendanceId);
    return ResponseEntity.created(generateQrUri(attendanceId, qrUuid))
      .body(SuccessResponse.messageOnly());
  }

  @DeleteMapping("/{attendanceId}/qr")
  public ResponseEntity<SuccessResponse> expireQr(
    @PathVariable Long attendanceId, @RequestParam String qrUuid) {
    attendanceService.expireQr(attendanceId, qrUuid);
    return ResponseEntity.noContent().build();
  }

  private URI generateAttendanceUri(Long attendanceId) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(attendanceId)
      .toUri();
  }

  private URI generateQrUri(Long attendanceId, String qrUuid) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}/qr")
      .queryParam("qrUuid", qrUuid)
      .buildAndExpand(attendanceId)
      .toUri();
  }
}
