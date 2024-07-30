package gdsc.konkuk.platformcore.domain.email.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;

public interface EmailTaskRepository extends JpaRepository<EmailTask, Long> {}
