package gdsc.konkuk.platformcore.application.member.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MemberUpdateCommand {

    @NotNull
    private Long memberId;

    private String studentId;
    private String name;
    private String email;
    private String department;
    private String batch;
    private String role;
}
