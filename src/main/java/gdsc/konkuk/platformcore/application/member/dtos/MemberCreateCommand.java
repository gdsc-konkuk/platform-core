package gdsc.konkuk.platformcore.application.member.dtos;

import gdsc.konkuk.platformcore.domain.member.entity.Member;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class MemberCreateCommand {

    @NotEmpty
    private final String studentId;

    @NotEmpty
    private final String name;

    @NotEmpty
    private final String email;

    @NotEmpty
    private final String department;

    @NotEmpty
    private final String batch;

    @NotEmpty
    private final String role;

    public MemberCreateCommand(String studentId, String name, String email, String department,
            String batch, String role) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.batch = batch;
        this.role = role;
    }

    public static Member toEntity(MemberCreateCommand command) {
        return Member.builder()
                .studentId(command.getStudentId())
                .name(command.getName())
                .email(command.getEmail())
                .department(command.getDepartment())
                .batch(command.getBatch())
                .role(command.getRole())
                .build();
    }
}
