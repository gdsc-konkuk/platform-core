package gdsc.konkuk.platformcore.fixture.member;

import static gdsc.konkuk.platformcore.fixture.member.MemberFixture.*;

import gdsc.konkuk.platformcore.controller.member.dtos.MemberRegisterRequest;

public class MemberRegisterRequestFixture {
  public static MemberRegisterRequest getGeneralMember1RegisterRequest(){
    return MemberRegisterRequest.builder()
      .memberId(GENERAL_1_MEMBER_ID)
      .password(GENERAL_PASSWORD)
      .email(GENERAL_1_EMAIL)
      .name(GENERAL_1_NAME)
      .department(GENERAL_1_DEPARTMENT)
      .batch(BATCH)
      .build();
  }}
