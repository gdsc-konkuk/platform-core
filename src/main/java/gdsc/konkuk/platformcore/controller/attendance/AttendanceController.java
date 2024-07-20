package gdsc.konkuk.platformcore.controller.attendance;

import gdsc.konkuk.platformcore.application.attendance.AttendanceService;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participants;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {
  private final AttendanceService attendanceService;

  @PostMapping("/{eventId}")
  public ResponseEntity<SuccessResponse> attend(
      @PathVariable Long eventId, @AuthenticationPrincipal OidcUser principal) {
    String memberEmail = principal.getEmail();
    Participants participants = attendanceService.attend(memberEmail, eventId);
    return ResponseEntity.ok(SuccessResponse.of(participants));
  }
}
