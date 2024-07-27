package gdsc.konkuk.platformcore.domain.retrospect.repository;

import gdsc.konkuk.platformcore.domain.retrospect.entity.Retrospect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetrospectRepository extends JpaRepository<Retrospect, Long> {
  Optional<Retrospect> findByEventId(Long eventId);
}
