package gdsc.konkuk.platformcore.domain.event.repository;

import gdsc.konkuk.platformcore.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
  List<Event> findAllByStartAtBetween(LocalDateTime st, LocalDateTime en);
}
