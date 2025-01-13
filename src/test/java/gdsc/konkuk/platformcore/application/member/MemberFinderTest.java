package gdsc.konkuk.platformcore.application.member;

import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import gdsc.konkuk.platformcore.fixture.member.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


class MemberFinderTest {

  private MemberFinder subject;

  @Mock
  private MemberRepository memberRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    subject = new MemberFinder(memberRepository);
  }

  @DisplayName("fetchMemberById : 해당하는 멤버가 없으면 UserNotFoundException 발생")
  @Test
  void should_throw_UserNotFoundException_when_member_not_found() {
    // given
    Long memberId = 1L;
    given(memberRepository.findById(any(Long.class))).willReturn(Optional.empty());

    // when
    Executable actual = () -> subject.fetchMemberById(memberId);

    // then
    assertThrows(UserNotFoundException.class, actual);
  }

  @DisplayName("fetchMemberByIdsAndBatch : 실제 조회된 유저수와 요청한 유저수가 다르면 UserNotFoundException 발생")
  @Test
  void should_throw_UserNotFoundException_when_member_count_not_match() {
    // given
    final List<Long> memberIds = List.of(1L, 2L, 3L);
    final String batch = "24-25";
    given(memberRepository.findAllByIdsAndBatch(any(), any())).willReturn(
        List.of(
            MemberFixture.builder().batch(batch).build().getFixture(),
            MemberFixture.builder().batch(batch).build().getFixture()));
    // when
    Executable actual = () -> subject.fetchMembersByIdsAndBatch(memberIds, batch);
    // then
    assertThrows(UserNotFoundException.class, actual);
  }
}