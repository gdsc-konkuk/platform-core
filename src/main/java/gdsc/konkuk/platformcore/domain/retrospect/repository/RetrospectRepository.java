package gdsc.konkuk.platformcore.domain.retrospect.repository;

import gdsc.konkuk.platformcore.domain.retrospect.entity.Retrospect;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetrospectRepository extends JpaRepository<Retrospect, Long> {}
