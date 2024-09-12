package gdsc.konkuk.platformcore.fixture.member;

import gdsc.konkuk.platformcore.controller.member.dtos.PasswordChangeRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PasswordChangeRequestFixture {
  private final PasswordChangeRequest fixture;

  @Builder
  public PasswordChangeRequestFixture(String password) {
    this.fixture = PasswordChangeRequest.builder()
      .password(password)
      .build();
  }
}
