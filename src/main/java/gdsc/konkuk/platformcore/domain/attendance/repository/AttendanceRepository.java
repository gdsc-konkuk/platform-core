package gdsc.konkuk.platformcore.domain.attendance.repository;

import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {}
