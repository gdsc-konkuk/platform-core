package gdsc.konkuk.platformcore.controller.event;

import gdsc.konkuk.platformcore.application.attendance.AttendanceService;
import gdsc.konkuk.platformcore.application.event.EventBrief;
import gdsc.konkuk.platformcore.application.event.EventService;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
  private final EventService eventService;
  private final AttendanceService attendanceService;

  @GetMapping
  public ResponseEntity<SuccessResponse> getEventsOfTheMonth(
      @RequestParam Integer year, @RequestParam Integer month) {
    List<EventBrief> eventBriefs = eventService.getEventsOfTheMonth(LocalDate.of(year, month, 1));
    return ResponseEntity.ok(SuccessResponse.of(eventBriefs));
  }

  @PostMapping("/{eventId}/attendance")
  public ResponseEntity<SuccessResponse> registerAttendance(@PathVariable Long eventId) {
    attendanceService.registerAttendance(eventId);
    URI location = URI.create("/api/v1/attendances/" + eventId);
    return ResponseEntity.created(location).body(SuccessResponse.messageOnly());
  }

  // TODO
  // 출석 key를 발급하고 회원은 반드시 해당 key를 포함하는 URL로 접속하도록 해야 함
  // 현 상황에서는 누구나 출석 URL을 쉽게 유추하여 QR 인식 없이도 출석할 수 있음
  // 또한, key를 발급할 경우 key 만료를 통해 출석 세션을 종료하는 기능을 쉽게 추가할 수 있음
  @DeleteMapping("/{eventId}/attendance")
  public ResponseEntity<SuccessResponse> deleteAttendance(@PathVariable Long eventId) {
    attendanceService.deleteAttendance(eventId);
    return ResponseEntity.ok(SuccessResponse.messageOnly());
  }
}
