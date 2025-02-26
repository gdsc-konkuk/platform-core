package gdsc.konkuk.platformcore.util.fixture.member;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.controller.member.dtos.MemberRegisterRequest;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberRegisterRequestFixture {

    private final MemberRegisterRequest fixture;

    @Builder
    public MemberRegisterRequestFixture(String studentId, String email, String name,
            String department, String batch, MemberRole role) {
        this.fixture = MemberRegisterRequest.builder()
                .studentId(getDefault(studentId, "2024000000"))
                .email(getDefault(email, "ex@gmail.com"))
                .name(getDefault(name, "name"))
                .department(getDefault(department, "department"))
                .batch(getDefault(batch, "24-25"))
                .role(getDefault(role, MemberRole.MEMBER).toString())
                .build();
    }
}
