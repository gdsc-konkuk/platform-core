package gdsc.konkuk.platformcore.domain.attendance.repository;

import gdsc.konkuk.platformcore.domain.attendance.entity.Participants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantsRepository extends JpaRepository<Participants, Long> {
  void deleteAllByEventId(Long eventId);

  Optional<Participants> findByMemberIdAndEventId(Long memberId, Long eventId);
}
