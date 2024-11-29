package gdsc.konkuk.platformcore.domain.attendance.repository;

import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceQueryDto;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import java.time.LocalDateTime;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
  @Query("SELECT a FROM Attendance a WHERE a.attendanceTime BETWEEN :st AND :en")
  List<Attendance> findAllByPeriod(LocalDateTime st, LocalDateTime en);

  @Query("SELECT new gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceQueryDto(" +
      "m.id, m.name, m.role, m.department, p.id, a.id, a.attendanceTime, p.attendanceType) " +
      "FROM Attendance a " +
      "LEFT JOIN Participant p ON p.attendance.id = a.id " +
      "LEFT JOIN Member m ON m.id = p.memberId " +
      "WHERE m.batch = :batch " +
      "AND a.attendanceTime BETWEEN :st AND :en")
  List<MemberAttendanceQueryDto> findAllAttendanceInfoByBatchAndPeriod(String batch, LocalDateTime st, LocalDateTime en);
}
