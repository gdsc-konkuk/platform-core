package gdsc.konkuk.platformcore.controller.member.dtos;

import gdsc.konkuk.platformcore.application.member.dtos.MemberUpdateCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberUpdateInfo {

    @NotNull
    private Long memberId;

    private String studentId;
    private String name;
    private String email;
    private String department;
    private String batch;
    private String role;

    public MemberUpdateCommand toCommand() {
        return MemberUpdateCommand.builder()
                .memberId(memberId)
                .studentId(studentId)
                .name(name)
                .email(email)
                .department(department)
                .batch(batch)
                .role(role)
                .build();
    }
}
