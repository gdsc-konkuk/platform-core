package gdsc.konkuk.platformcore.domain.member.entity;

public enum MemberRole {
    ROLE_LEAD("ROLE_LEAD"),
    ROLE_CORE("ROLE_CORE"),
    ROLE_MEMBER("ROLE_MEMBER");

    private final String authority;

    MemberRole(String authority) {
        this.authority = authority;
    }

    public static MemberRole from(String role) {
        return switch (role) {
            case "ROLE_LEAD" -> ROLE_LEAD;
            case "ROLE_CORE" -> ROLE_CORE;
            case "ROLE_MEMBER" -> ROLE_MEMBER;
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };
    }

    @Override
    public String toString() {
        return this.authority;
    }
}
