package gdsc.konkuk.platformcore.domain.email.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gdsc.konkuk.platformcore.domain.email.entity.Email;

public interface EmailRepository extends JpaRepository<Email, Long>{
}
