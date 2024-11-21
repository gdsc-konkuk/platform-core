package gdsc.konkuk.platformcore.controller.member.dtos;

import gdsc.konkuk.platformcore.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberInfo {
  private Long memberId;
  private String studentId;
  private String name;
  private String email;
  private String department;
  private String batch;
  private String role;

  public static MemberInfo from(Member member) {
    return MemberInfo.builder()
        .memberId(member.getId())
        .studentId(member.getStudentId())
        .name(member.getName())
        .email(member.getEmail())
        .department(member.getDepartment())
        .batch(member.getBatch())
        .role(member.getRole().toString())
        .build();
  }
}
