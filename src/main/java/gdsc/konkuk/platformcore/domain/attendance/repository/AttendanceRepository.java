package gdsc.konkuk.platformcore.domain.attendance.repository;

import gdsc.konkuk.platformcore.application.attendance.AttendanceInfo;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
  @Query(
      """
      SELECT new gdsc.konkuk.platformcore.application.attendance.AttendanceInfo(
        a.id, a.eventId, p.memberId, p.id, e.startAt, p.attendance
      )
      FROM Attendance a
      LEFT JOIN Event e ON a.eventId = e.id
      LEFT JOIN Participant p ON a.id = p.attendanceId
      WHERE e.startAt BETWEEN :st AND :en
      """)
  List<AttendanceInfo> findAllAttendanceInfoByStartAtBetween(LocalDateTime st, LocalDateTime en);
}
