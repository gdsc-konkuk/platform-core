package gdsc.konkuk.platformcore.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gdsc.konkuk.platformcore.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByMemberId(String memberId);

  Optional<Member> findByEmail(String memberEmail);

  List<Member> findAllByBatch(String batch);
}
