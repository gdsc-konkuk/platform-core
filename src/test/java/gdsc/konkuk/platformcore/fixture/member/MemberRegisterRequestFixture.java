package gdsc.konkuk.platformcore.fixture.member;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.controller.member.dtos.MemberRegisterRequest;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberRegisterRequestFixture {
  private final MemberRegisterRequest fixture;

  @Builder
  public MemberRegisterRequestFixture(String memberId, String password, String email, String name, String department, String batch, MemberRole role) {
    this.fixture = MemberRegisterRequest.builder()
      .memberId(getDefault(memberId, "2024000000"))
      .password(getDefault(password, "password"))
      .email(getDefault(email, "ex@gmail.com"))
      .name(getDefault(name, "name"))
      .department(getDefault(department, "department"))
      .batch(getDefault(batch, "24-25"))
      .role(getDefault(role, MemberRole.MEMBER).toString())
      .build();
  }
}
