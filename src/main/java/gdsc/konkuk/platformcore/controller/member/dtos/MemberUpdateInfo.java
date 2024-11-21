package gdsc.konkuk.platformcore.controller.member.dtos;

import gdsc.konkuk.platformcore.domain.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberUpdateInfo {
  @NotNull private Long memberId;
  private String studentId;
  private String name;
  private String email;
  private String department;
  private String batch;
  private String role;

  public void updateEntity(Member member) {
    if (studentId != null) {
      member.updateStudentId(studentId);
    }
    if (name != null) {
      member.updateName(name);
    }
    if (email != null) {
      member.updateEmail(email);
    }
    if (department != null) {
      member.updateDepartment(department);
    }
    if (batch != null) {
      member.updateBatch(batch);
    }
    if (role != null) {
      member.updateRole(role);
    }
  }
}
