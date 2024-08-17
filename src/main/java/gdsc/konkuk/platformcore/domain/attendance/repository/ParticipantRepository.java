package gdsc.konkuk.platformcore.domain.attendance.repository;

import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
  void deleteAllByAttendanceId(Long attendanceId);

  Optional<Participant> findByMemberIdAndAttendanceId(Long memberId, Long attendanceId);

  @Query(
      """
      SELECT p
      FROM Member m
      LEFT JOIN Participant p ON m.id = p.memberId
      LEFT JOIN Attendance a ON p.attendance.id = a.id
      LEFT JOIN Event e ON a.eventId = e.id
      WHERE m.batch = :batch AND e.startAt BETWEEN :st AND :en
      """)
  List<Participant> findAllByBatchAndStartAtBetween(
      String batch, LocalDateTime st, LocalDateTime en);
}
