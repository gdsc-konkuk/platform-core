package gdsc.konkuk.platformcore.domain.member.entity;

public enum MemberRole {
  LEAD("ROLE_LEAD"),
  ADMIN("ROLE_ADMIN"),
  MEMBER("ROLE_MEMBER");

  private final String authority;

  public static MemberRole from(String role) {
    return switch (role) {
      case "ROLE_LEAD" -> LEAD;
      case "ROLE_ADMIN" -> ADMIN;
      case "ROLE_MEMBER" -> MEMBER;
      default -> throw new IllegalArgumentException("Invalid role: " + role);
    };
  }

  MemberRole(String authority) {
    this.authority = authority;
  }

  @Override
  public String toString() {
    return this.authority;
  }
}
