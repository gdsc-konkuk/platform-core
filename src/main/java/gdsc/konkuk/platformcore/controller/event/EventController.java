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

  @DeleteMapping("/{eventId}/attendance")
  public ResponseEntity<SuccessResponse> deleteAttendance(@PathVariable Long eventId) {
    attendanceService.deleteAttendance(eventId);
    return ResponseEntity.ok(SuccessResponse.messageOnly());
  }
}
