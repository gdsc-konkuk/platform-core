package gdsc.konkuk.platformcore.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gdsc.konkuk.platformcore.domain.member.entity.Member;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
  @Query("SELECT m FROM Member m WHERE m.batch = :batch AND m.isDeleted = false")
  List<Member> findAllActiveByBatch(String batch);

  Optional<Member> findByStudentId(String studentId);

  Optional<Member> findByEmail(String memberEmail);

  List<Member> findAllByBatch(String batch);
}
