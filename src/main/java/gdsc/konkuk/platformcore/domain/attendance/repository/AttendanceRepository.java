package gdsc.konkuk.platformcore.domain.attendance.repository;

import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceQueryDto;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
  Optional<Attendance> findByEventId(Long aLong);
  
  @Query("SELECT new gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceQueryDto(" +
      "e.id, m.id, m.name, m.role, m.department, p.id, a.id, e.startAt, p.isAttended) " +
      "FROM Attendance a " +
      "left JOIN Participant p ON p.attendance.id = a.id " +
      "left JOIN Event e ON p.attendance.eventId = e.id " +
      "left JOIN Member m ON m.id = p.memberId " +
      "WHERE m.batch = :batch " +
      "AND e.startAt BETWEEN :st AND :en")
  List<MemberAttendanceQueryDto> findAllAttendanceInfoByBatchAndPeriod(String batch, LocalDateTime st, LocalDateTime en);
}
