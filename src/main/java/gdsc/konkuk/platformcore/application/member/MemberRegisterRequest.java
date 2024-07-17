package gdsc.konkuk.platformcore.application.member;

import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberRegisterRequest {
	private String memberId;
	private String password;
	private String name;
	private String email;
	private MemberRole memberRole;
	private int batch;

	public static Member toEntity(MemberRegisterRequest request) {
		return Member.builder()
				.memberId(request.getMemberId())
				.password(request.getPassword())
				.name(request.getName())
				.email(request.getEmail())
				.role(request.getMemberRole())
				.batch(request.getBatch())
				.build();
	}
}
