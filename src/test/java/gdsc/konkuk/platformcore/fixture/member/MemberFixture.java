package gdsc.konkuk.platformcore.fixture.member;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberFixture {

    private final Member fixture;

    @Builder
    public MemberFixture(Long id, String studentId, String name, String email, String department,
            MemberRole role, String batch) {
        this.fixture = Member.builder()
                .id(getDefault(id, 0L))
                .studentId(getDefault(studentId, "202400000"))
                .name(getDefault(name, "name"))
                .email(getDefault(email, "ex@gmail.com"))
                .department(getDefault(department, "department"))
                .role(getDefault(role, MemberRole.MEMBER).toString())
                .batch(getDefault(batch, "24-25"))
                .build();
    }
}
