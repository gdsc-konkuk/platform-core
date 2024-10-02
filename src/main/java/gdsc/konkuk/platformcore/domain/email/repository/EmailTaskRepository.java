package gdsc.konkuk.platformcore.domain.email.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import org.springframework.data.jpa.repository.Query;

public interface EmailTaskRepository extends JpaRepository<EmailTask, Long> {

  @Query("SELECT e FROM EmailTask e WHERE e.isSent = false")
  List<EmailTask> findAllWhereNotSent();

  Page<EmailTask> findAll(Pageable pageable);
}
