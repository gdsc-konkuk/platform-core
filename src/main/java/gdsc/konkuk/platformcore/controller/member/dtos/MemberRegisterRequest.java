package gdsc.konkuk.platformcore.controller.member.dtos;

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
  private String studentId;
  @NotEmpty
  private String name;
  @NotEmpty
  private String email;
  @NotEmpty
  private String department;
  @NotEmpty
  private String batch;
  @NotEmpty
  private String role;

  public static Member toEntity(MemberRegisterRequest request) {
    return Member.builder()
      .studentId(request.getStudentId())
      .password("") // `PasswordEncoder`로 생성할 수 없는 문자열 (로그인 불가해야 함)
      .name(request.getName())
      .email(request.getEmail())
      .department(request.getDepartment())
      .batch(request.getBatch())
      .role(request.getRole())
      .build();
  }
}
