package gdsc.konkuk.platformcore.domain.email.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gdsc.konkuk.platformcore.domain.email.entity.PlatformEmail;

public interface EmailRepository extends JpaRepository<PlatformEmail, Long> {
}
