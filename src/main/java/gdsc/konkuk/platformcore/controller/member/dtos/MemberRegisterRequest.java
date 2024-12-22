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
                .name(request.getName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .batch(request.getBatch())
                .role(request.getRole())
                .build();
    }
}
