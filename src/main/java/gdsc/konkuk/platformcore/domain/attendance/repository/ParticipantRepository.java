package gdsc.konkuk.platformcore.domain.attendance.repository;

import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
  void deleteAllByAttendanceId(Long attendanceId);

  Optional<Participant> findByMemberIdAndAttendanceId(Long memberId, Long attendanceId);
}
