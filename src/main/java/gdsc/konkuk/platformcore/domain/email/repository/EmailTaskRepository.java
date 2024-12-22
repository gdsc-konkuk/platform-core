package gdsc.konkuk.platformcore.domain.email.repository;

import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmailTaskRepository extends JpaRepository<EmailTask, Long> {

    @Query("SELECT e FROM EmailTask e WHERE e.isSent = false")
    List<EmailTask> findAllWhereNotSent();

    @Query("SELECT e FROM EmailTask e " +
            "order by e.sendAt desc")
    List<EmailTask> findAll();
}
