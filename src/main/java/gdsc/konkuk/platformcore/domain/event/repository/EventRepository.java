package gdsc.konkuk.platformcore.domain.event.repository;

import gdsc.konkuk.platformcore.application.event.EventWithAttendance;
import gdsc.konkuk.platformcore.domain.event.entity.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
  @Query(
      """
      SELECT new gdsc.konkuk.platformcore.application.event.EventWithAttendance(
        e.id, a.id, e.title, e.startAt
      )
      FROM Event e
        lEFT JOIN Attendance a ON e.id = a.eventId
      WHERE e.startAt BETWEEN :st AND :en
      """)
  List<EventWithAttendance> findAllWithAttendanceByStartAtBetween(
      LocalDateTime st, LocalDateTime en);

  @Query(
      """
      SELECT e
      FROM Event e
      LEFT JOIN FETCH e.eventImageList
      """)
  List<Event> findAll();
}
