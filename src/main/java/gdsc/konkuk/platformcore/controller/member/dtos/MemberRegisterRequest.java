package gdsc.konkuk.platformcore.controller.member.dtos;

import gdsc.konkuk.platformcore.application.member.dtos.MemberCreateCommand;
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

    public MemberCreateCommand toCommand() {
        return new MemberCreateCommand(studentId, name, email, department, batch, role);
    }

}
