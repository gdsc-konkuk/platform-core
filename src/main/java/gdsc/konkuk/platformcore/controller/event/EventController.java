package gdsc.konkuk.platformcore.controller.event;

import gdsc.konkuk.platformcore.application.event.EventService;
import gdsc.konkuk.platformcore.application.event.EventWithAttendance;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
  private final EventService eventService;

  @GetMapping
  public ResponseEntity<SuccessResponse> getEventsOfTheMonthWithAttendance(
      @RequestParam Integer year, @RequestParam Integer month) {
    List<EventWithAttendance> eventsOfMonthWithAttendance =
        eventService.getEventsOfTheMonthWithAttendance(LocalDate.of(year, month, 1));
    return ResponseEntity.ok(SuccessResponse.of(eventsOfMonthWithAttendance));
  }
}
