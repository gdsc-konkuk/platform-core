package gdsc.konkuk.platformcore.domain.email.repository;

import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailReceiverRepository extends JpaRepository<EmailReceiver, Long> {
}
