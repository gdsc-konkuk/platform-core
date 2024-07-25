package gdsc.konkuk.platformcore.controller.member;

import gdsc.konkuk.platformcore.domain.member.entity.Member;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberRegisterRequest {
  @NotEmpty
  private String memberId;
  @NotEmpty
  private String password;
  @NotEmpty
  private String name;
  @NotEmpty
  private String email;
  private String batch;

  public static Member toEntity(MemberRegisterRequest request) {
    return Member.builder()
      .memberId(request.getMemberId())
      .password(request.getPassword())
      .name(request.getName())
      .email(request.getEmail())
      .batch(request.getBatch())
      .build();
  }
}
