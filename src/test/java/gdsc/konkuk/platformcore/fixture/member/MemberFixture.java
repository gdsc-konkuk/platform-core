package gdsc.konkuk.platformcore.fixture.member;

import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;

/**
 * MemberFixture
 * - AdminFixture
 * - GeneralFixture1
 * - GeneralFixture2
 * - GeneralFixture3
 */

public class MemberFixture {
  public static final String WRONG_PASSWORD = "wrong_password";
  public static final String BATCH = "24-25";

  public static final Long ADMIN_ID = 1L;
  public static final String ADMIN_MEMBER_ID = "202400000";
  public static final String ADMIN_PASSWORD = "password";
  public static final String ADMIN_PASSWORD_ENCODED = "$2a$10$d7DjseDroHsRGVGR1zDUL.q7uwAQ2aH4nHM1JiQ1OFV.D0qUTl7w.";
  public static final String ADMIN_NAME = "admin";
  public static final String ADMIN_EMAIL = "gdsc.konkuk@gmail.com";
  public static final String ADMIN_DEPARTMENT = "GDSC Konkuk";
  public static final MemberRole ADMIN_ROLE = MemberRole.ADMIN;

  public static final String GENERAL_PASSWORD = "password";
  public static final String GENERAL_PASSWORD_ENCODED = "$2a$10$d7DjseDroHsRGVGR1zDUL.q7uwAQ2aH4nHM1JiQ1OFV.D0qUTl7w.";
  public static final MemberRole GENERAL_ROLE = MemberRole.MEMBER;

  public static final Long GENERAL_1_ID = 2L;
  public static final String GENERAL_1_MEMBER_ID = "202400001";
  public static final String GENERAL_1_NAME = "general1";
  public static final String GENERAL_1_EMAIL = "ex1@gmail.com";
  public static final String GENERAL_1_DEPARTMENT = "Computer Science";

  public static final Long GENERAL_2_ID = 3L;
  public static final String GENERAL_2_MEMBER_ID = "202400002";
  public static final String GENERAL_2_NAME = "general2";
  public static final String GENERAL_2_EMAIL = "ex2@gmail.com";
  public static final String GENERAL_2_DEPARTMENT = "Art";

  public static final Long GENERAL_3_ID = 4L;
  public static final String GENERAL_3_MEMBER_ID = "202400001";
  public static final String GENERAL_3_NAME = "general1";
  public static final String GENERAL_3_EMAIL = "ex1@gmail.com";
  public static final String GENERAL_3_DEPARTMENT = "Business";

  public static Member getAdminMemberFixture(){
    return Member.builder()
      .id(ADMIN_ID)
      .memberId(ADMIN_MEMBER_ID)
      .password(ADMIN_PASSWORD_ENCODED)
      .name(ADMIN_NAME)
      .email(ADMIN_EMAIL)
      .department(ADMIN_DEPARTMENT)
      .role(ADMIN_ROLE.toString())
      .batch(BATCH)
      .build();
  }

  public static Member getGeneralMemberFixture1(){
    return Member.builder()
      .id(GENERAL_1_ID)
      .memberId(GENERAL_1_MEMBER_ID)
      .password(GENERAL_PASSWORD_ENCODED)
      .name(GENERAL_1_NAME)
      .email(GENERAL_1_EMAIL)
      .department(GENERAL_1_DEPARTMENT)
      .role(GENERAL_ROLE.toString())
      .batch(BATCH)
      .build();
  }

  public static Member getGeneralMemberFixture2(){
    return Member.builder()
      .id(GENERAL_2_ID)
      .memberId(GENERAL_2_MEMBER_ID)
      .password(GENERAL_PASSWORD_ENCODED)
      .name(GENERAL_2_NAME)
      .email(GENERAL_2_EMAIL)
      .department(GENERAL_2_DEPARTMENT)
      .role(GENERAL_ROLE.toString())
      .batch(BATCH)
      .build();
  }

  public static Member getGeneralMemberFixture3(){
    return Member.builder()
      .id(GENERAL_3_ID)
      .memberId(GENERAL_3_MEMBER_ID)
      .password(GENERAL_PASSWORD_ENCODED)
      .name(GENERAL_3_NAME)
      .email(GENERAL_3_EMAIL)
      .department(GENERAL_3_DEPARTMENT)
      .role(GENERAL_ROLE.toString())
      .batch(BATCH)
      .build();
  }
}
