package gdsc.konkuk.platformcore.controller.member;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.konkuk.platformcore.annotation.RestDocsTest;
import gdsc.konkuk.platformcore.application.auth.JwtTokenProvider;
import gdsc.konkuk.platformcore.application.member.MemberService;
import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyExistException;
import gdsc.konkuk.platformcore.controller.member.dtos.AttendanceUpdateInfo;
import gdsc.konkuk.platformcore.controller.member.dtos.AttendanceUpdateRequest;
import gdsc.konkuk.platformcore.controller.member.dtos.MemberRegisterRequest;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import gdsc.konkuk.platformcore.fixture.member.MemberAttendancesFixture;
import gdsc.konkuk.platformcore.fixture.member.MemberFixture;
import gdsc.konkuk.platformcore.fixture.member.MemberRegisterRequestFixture;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RestDocsTest
@SpringBootTest
class MemberControllerTest {

  MockMvc mockMvc;

  @MockBean private MemberService memberService;

  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtTokenProvider jwtTokenProvider;

  @BeforeEach
  void setUp(
      WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .apply(documentationConfiguration(restDocumentation))
            .build();
  }

  @Test
  @DisplayName("새로운 멤버 회원 가입 성공")
  void should_success_when_newMember() throws Exception {
    // given
    MemberRegisterRequest memberRegisterRequest = MemberRegisterRequestFixture.builder()
        .studentId("202400000").build().getFixture();
    Member memberToRegister = MemberFixture.builder()
        .studentId("202400000").build().getFixture();
    given(memberService.register(any(MemberRegisterRequest.class))).willReturn(memberToRegister);

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/members")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRegisterRequest))
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isCreated())
        .andDo(
            document(
                "member/register",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("새로운 멤버 회원 가입 성공")
                        .tag("member")
                        .responseHeaders(headerWithName("Location").description("등록한 Member URI"))
                        .requestFields(
                            fieldWithPath("studentId").description("회원 아이디"),
                            fieldWithPath("email").description("이메일"),
                            fieldWithPath("name").description("이름"),
                            fieldWithPath("department").description("학과"),
                            fieldWithPath("batch").description("배치"),
                            fieldWithPath("role").description("역할"))
                        .responseFields(
                            fieldWithPath("success").description(true),
                            fieldWithPath("message").description("회원 가입 성공"),
                            fieldWithPath("data").description("null"))
                        .build())));
  }

  @Test
  @DisplayName("이미 존재하는 유저 회원 가입 실패")
  void should_fail_when_existingMember() throws Exception {
    // given
    MemberRegisterRequest memberRegisterRequest = MemberRegisterRequestFixture.builder().build().getFixture();
    given(memberService.register(any(MemberRegisterRequest.class)))
        .willThrow(UserAlreadyExistException.class);

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/members")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRegisterRequest))
                .with(csrf()));

    // then
    result.andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("회원 탈퇴 성공")
  void should_success_when_delete_member() throws Exception {
    // given
    Member member = MemberFixture.builder().role(MemberRole.MEMBER).build().getFixture();
    String jwt = jwtTokenProvider.createToken(member);
    Member memberToWithdraw = MemberFixture.builder().build().getFixture();
    willDoNothing().given(memberService).withdraw(memberToWithdraw.getId());

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/members")
                .header("Authorization", "Bearer " + jwt)
                .contentType(APPLICATION_JSON)
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "member/delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("존재하는 회원 탈퇴")
                        .tag("member")
                        .requestHeaders(headerWithName("Authorization").description("Bearer 토큰"))
                        .build())));
  }

  @Test
  @DisplayName("특정 배치의 특정 월의 멤버 출석 정보 조회 성공")
  void should_success_when_get_attendances_by_batch() throws Exception {
    // given
    Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
    String jwt = jwtTokenProvider.createToken(member);
    // TODO: 좀 더 상세하고 정확한 Fixture 필요 (`batch`, `eventId`, `participantId` 등)
    given(memberService.getMemberAttendanceWithBatchAndPeriod(anyString(), any()))
        .willReturn(
            List.of(
                MemberAttendancesFixture.builder().memberId(1L).memberName("member1").build().getFixture(),
                MemberAttendancesFixture.builder().memberId(2L).memberName("member2").build().getFixture(),
                MemberAttendancesFixture.builder().memberId(3L).memberName("member3").build().getFixture()));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/members/{batch}/attendances", "24-25")
                .header("Authorization", "Bearer " + jwt)
                .param("year", "2024")
                .param("month", "07")
                .contentType(APPLICATION_JSON)
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(
            document(
                "member/attendances",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("특정 달의 멤버 출석 정보 조회")
                        .tag("member")
                        .requestHeaders(headerWithName("Authorization").description("Bearer 토큰"))
                        .pathParameters(parameterWithName("batch").description("조회할 멤버 기수"))
                        .queryParameters(
                            parameterWithName("year").description("조회할 년도"),
                            parameterWithName("month").description("조회할 월"),
                            parameterWithName("_csrf").ignored())
                        .responseFields(
                            fieldWithPath("success").description(true),
                            fieldWithPath("message").description("멤버 출석 정보 조회 성공"),
                            fieldWithPath("data").description("멤버 출석 정보 리스트"),
                            fieldWithPath("data[].memberId").description("멤버 아이디"),
                            fieldWithPath("data[].memberName").description("멤버 이름"),
                            fieldWithPath("data[].memberRole").description("멤버 역할"),
                            // fieldWithPath("data[].profileImageUrl").description("멤버 프로필 이미지"),
                            fieldWithPath("data[].department").description("멤버 학과"),
                            fieldWithPath("data[].totalAttendances").description("전체 등록 횟수"),
                            fieldWithPath("data[].actualAttendances").description("실제 출석 횟수"),
                            fieldWithPath("data[].attendanceInfoList").description("멤버 출석 정보 리스트"),
                            fieldWithPath("data[].attendanceInfoList[].attendanceId")
                                .description("출석 아이디"),
                            fieldWithPath("data[].attendanceInfoList[].memberId")
                                .description("멤버 아이디"),
                            fieldWithPath("data[].attendanceInfoList[].eventId")
                                .description("이벤트 아이디"),
                            fieldWithPath("data[].attendanceInfoList[].participantId")
                                .description("참가자 아이디"),
                            fieldWithPath("data[].attendanceInfoList[].attendanceDate")
                                .description("출석 날짜"),
                            fieldWithPath("data[].attendanceInfoList[].attended")
                                .description("출석 여부"))
                        .build())));
  }

  @Test
  @DisplayName("특정 배치의 특정 월의 멤버 출석 정보 수정 성공")
  void should_success_when_update_attendances_by_batch() throws Exception {
    // given
    Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
    String jwt = jwtTokenProvider.createToken(member);
    List<AttendanceUpdateInfo> attendanceUpdateInfoList = List.of(
            AttendanceUpdateInfo.builder().participantId(1L).isAttended(true).build(),
            AttendanceUpdateInfo.builder().participantId(2L).isAttended(false).build(),
            AttendanceUpdateInfo.builder().participantId(3L).isAttended(true).build());
    AttendanceUpdateRequest attendanceUpdateRequest = new AttendanceUpdateRequest(attendanceUpdateInfoList);
    willDoNothing().given(memberService).updateAttendances(
            "24-25",
            LocalDate.of(2024, 7, 1),
            attendanceUpdateInfoList);

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/members/{batch}/attendances", "24-25")
                .header("Authorization", "Bearer " + jwt)
                .param("year", "2024")
                .param("month", "07")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(attendanceUpdateRequest))
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(
            document(
                "member/update_attendances",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("특정 달의 멤버 출석 정보 수정")
                        .tag("member")
                        .requestHeaders(headerWithName("Authorization").description("Bearer 토큰"))
                        .pathParameters(parameterWithName("batch").description("수정할 멤버 기수"))
                        .queryParameters(
                            parameterWithName("year").description("수정할 년도"),
                            parameterWithName("month").description("수정할 월"),
                            parameterWithName("_csrf").ignored())
                        .requestFields(
                            fieldWithPath("attendanceUpdateInfoList[]").description("출석 정보 수정 리스트"),
                            fieldWithPath("attendanceUpdateInfoList[].participantId")
                                .description("참가자 아이디"),
                            fieldWithPath("attendanceUpdateInfoList[].attended")
                                .description("출석 여부"))
                        .responseFields(
                            fieldWithPath("success").description(true),
                            fieldWithPath("message").description("멤버 출석 정보 수정 성공"),
                            fieldWithPath("data").description("null"))
                        .build())));
  }
}
