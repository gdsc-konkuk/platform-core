package gdsc.konkuk.platformcore.application.event;

import gdsc.konkuk.platformcore.domain.event.repository.EventRepository;
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

  public List<EventBrief> getEventsOfTheMonth(LocalDate month) {
    return eventRepository
        .findAllByStartAtBetween(
            month.withDayOfMonth(1).atStartOfDay(),
            month.withDayOfMonth(month.lengthOfMonth()).atTime(LocalTime.MAX))
        .stream()
        .map(EventBrief::from)
        .toList();
  }
}