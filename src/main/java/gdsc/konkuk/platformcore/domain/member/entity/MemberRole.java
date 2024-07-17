package gdsc.konkuk.platformcore.domain.member.entity;

public enum MemberRole {
	LEAD("ROLE_LEAD"),
	ADMIN("ROLE_ADMIN"),
	MEMBER("ROLE_MEMBER");

	private final String authority;

	MemberRole(String authority) {
		this.authority = authority;
	}

	@Override
	public String toString() {
		return this.authority;
	}
}
