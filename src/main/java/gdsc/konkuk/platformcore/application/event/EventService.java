package gdsc.konkuk.platformcore.application.event;

import gdsc.konkuk.platformcore.application.event.exceptions.EventErrorCode;
import gdsc.konkuk.platformcore.application.event.exceptions.EventNotFoundException;
import gdsc.konkuk.platformcore.application.retrospect.RetrospectService;
import gdsc.konkuk.platformcore.controller.event.EventRegisterRequest;
import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.domain.event.repository.EventRepository;
import gdsc.konkuk.platformcore.domain.retrospect.entity.Retrospect;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
  private final EventRepository eventRepository;
  private final RetrospectService retrospectService;

  @Transactional
  public Event register(EventRegisterRequest registerRequest) {
    Event newEvent = EventRegisterRequest.toEntity(registerRequest);
    Event savedEvent = eventRepository.saveAndFlush(newEvent);
    retrospectService.register(savedEvent, "내용이 없습니다.");
    return savedEvent;
  }

  public List<EventWithAttendance> getEventsOfTheMonthWithAttendance(LocalDate month) {
    return eventRepository.findAllWithAttendanceByStartAtBetween(
        month.withDayOfMonth(1).atStartOfDay(),
        month.withDayOfMonth(month.lengthOfMonth()).atTime(LocalTime.MAX));
  }

  public Retrospect getRetrospect(Long eventId) {
    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(() -> EventNotFoundException.of(EventErrorCode.EVENT_NOT_FOUND));
    return retrospectService.getRetrospectByEvent(event);
  }
}
