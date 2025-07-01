package gdsc.konkuk.platformcore.domain.email.repository;

import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailReceiverRepository extends JpaRepository<EmailReceiver, Long> {

    List<EmailReceiver> findEmailReceiversByEmailTaskId(Long emailTaskId);

    List<EmailReceiver> findByEmailTaskIdIn(List<Long> taskIds);
}
