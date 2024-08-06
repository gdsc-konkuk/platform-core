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

import gdsc.konkuk.platformcore.application.attendance.AttendanceInfo;
import gdsc.konkuk.platformcore.application.member.MemberAttendanceInfo;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;

import gdsc.konkuk.platformcore.annotation.CustomMockUser;
import gdsc.konkuk.platformcore.application.member.MemberService;
import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyExistException;
import gdsc.konkuk.platformcore.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
class MemberControllerTest {

  MockMvc mockMvc;

  @Mock Member member;

  @MockBean private MemberService memberService;

  @Autowired private ObjectMapper objectMapper;

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
    MemberRegisterRequest memberRegisterRequest =
        MemberRegisterRequest.builder()
            .memberId("202011288")
            .password("password")
            .email("example@konkuk.ac.kr")
            .name("홍길동")
            .batch("24-25")
            .build();
    given(memberService.register(any(MemberRegisterRequest.class))).willReturn(member);

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
                            fieldWithPath("memberId").description("회원 아이디"),
                            fieldWithPath("password").description("비밀번호"),
                            fieldWithPath("email").description("이메일"),
                            fieldWithPath("name").description("이름"),
                            fieldWithPath("batch").description("배치"))
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
    MemberRegisterRequest memberRegisterRequest =
        MemberRegisterRequest.builder()
            .memberId("202011288")
            .password("password")
            .email("example@konkuk.ac.kr")
            .name("홍길동")
            .batch("24-25")
            .build();
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
  @CustomMockUser
  void should_success_when_delete_member() throws Exception {
    // given

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/members")
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
                        .build())));
  }

  @Test
  @DisplayName("특정 배치의 특정 월의 멤버 출석 정보 조회 성공")
  @CustomMockUser
  void should_success_when_get_attendances_by_batch() throws Exception {
    // given
    // TODO: fixture로 변경
    given(memberService.getMemberAttendanceInfo(anyString(), any()))
        .willReturn(
            List.of(
                MemberAttendanceInfo.builder()
                    .memberId(0L)
                    .memberName("홍길동")
                    .department("컴퓨터공학과")
                    .memberRole(MemberRole.MEMBER)
                    .attendanceInfoList(
                        List.of(
                            AttendanceInfo.builder()
                                .attendanceId(1L)
                                .memberId(0L)
                                .eventId(1L)
                                .participantId(1L)
                                .attendanceDate(LocalDateTime.of(2024, 7, 3, 0, 0))
                                .attendance(true)
                                .build(),
                            AttendanceInfo.builder()
                                .attendanceId(2L)
                                .memberId(0L)
                                .eventId(2L)
                                .participantId(2L)
                                .attendanceDate(LocalDateTime.of(2024, 7, 5, 0, 0))
                                .attendance(false)
                                .build(),
                            AttendanceInfo.builder()
                                .attendanceId(3L)
                                .memberId(0L)
                                .eventId(3L)
                                .participantId(3L)
                                .attendanceDate(LocalDateTime.of(2024, 7, 8, 0, 0))
                                .attendance(true)
                                .build()))
                    .build(),
                MemberAttendanceInfo.builder()
                    .memberId(1L)
                    .memberName("전우치")
                    .department("기술경영학과")
                    .memberRole(MemberRole.MEMBER)
                    .attendanceInfoList(
                        List.of(
                            AttendanceInfo.builder()
                                .attendanceId(1L)
                                .memberId(1L)
                                .eventId(1L)
                                .participantId(4L)
                                .attendanceDate(LocalDateTime.of(2024, 7, 3, 0, 0))
                                .attendance(true)
                                .build(),
                            AttendanceInfo.builder()
                                .attendanceId(2L)
                                .memberId(1L)
                                .eventId(2L)
                                .participantId(5L)
                                .attendanceDate(LocalDateTime.of(2024, 7, 5, 0, 0))
                                .attendance(false)
                                .build(),
                            AttendanceInfo.builder()
                                .attendanceId(3L)
                                .memberId(1L)
                                .eventId(3L)
                                .participantId(6L)
                                .attendanceDate(LocalDateTime.of(2024, 7, 8, 0, 0))
                                .attendance(false)
                                .build()))
                    .build(),
                MemberAttendanceInfo.builder()
                    .memberId(2L)
                    .memberName("이순신")
                    .department("컴퓨터공학과")
                    .memberRole(MemberRole.MEMBER)
                    .attendanceInfoList(
                        List.of(
                            AttendanceInfo.builder()
                                .attendanceId(1L)
                                .memberId(2L)
                                .eventId(1L)
                                .participantId(7L)
                                .attendanceDate(LocalDateTime.of(2024, 7, 3, 0, 0))
                                .attendance(true)
                                .build(),
                            AttendanceInfo.builder()
                                .attendanceId(2L)
                                .memberId(2L)
                                .eventId(2L)
                                .participantId(8L)
                                .attendanceDate(LocalDateTime.of(2024, 7, 5, 0, 0))
                                .attendance(false)
                                .build(),
                            AttendanceInfo.builder()
                                .attendanceId(3L)
                                .memberId(2L)
                                .eventId(3L)
                                .participantId(9L)
                                .attendanceDate(LocalDateTime.of(2024, 7, 8, 0, 0))
                                .attendance(true)
                                .build()))
                    .build()));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/members/{batch}/attendances", "24-25")
                .param("year", "2024")
                .param("month", "7")
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
                            fieldWithPath("data[].attendanceInfoList[].attendance")
                                .description("출석 여부"))
                        .build())));
  }

  @Test
  @DisplayName("특정 배치의 특정 월의 멤버 출석 정보 수정 성공")
  @CustomMockUser
  void should_success_when_update_attendances_by_batch() throws Exception {
    // given
    List<AttendanceUpdateInfo> attendanceUpdateInfoList =
        List.of(
            AttendanceUpdateInfo.builder().participantId(1L).attendance(true).build(),
            AttendanceUpdateInfo.builder().participantId(2L).attendance(false).build(),
            AttendanceUpdateInfo.builder().participantId(3L).attendance(true).build());
    AttendanceUpdateRequest attendanceUpdateRequest =
        AttendanceUpdateRequest.builder()
            .attendanceUpdateInfoList(attendanceUpdateInfoList)
            .build();
    doNothing().when(memberService).updateAttendances(anyString(), any(), any());

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/members/{batch}/attendances", "24-25")
                .param("year", "2024")
                .param("month", "7")
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
                        .pathParameters(parameterWithName("batch").description("수정할 멤버 기수"))
                        .queryParameters(
                            parameterWithName("year").description("수정할 년도"),
                            parameterWithName("month").description("수정할 월"),
                            parameterWithName("_csrf").ignored())
                        .requestFields(
                            fieldWithPath("attendanceUpdateInfoList[]").description("출석 정보 수정 리스트"),
                            fieldWithPath("attendanceUpdateInfoList[].participantId")
                                .description("참가자 아이디"),
                            fieldWithPath("attendanceUpdateInfoList[].attendance")
                                .description("출석 여부"))
                        .responseFields(
                            fieldWithPath("success").description(true),
                            fieldWithPath("message").description("멤버 출석 정보 수정 성공"),
                            fieldWithPath("data").description("null"))
                        .build())));
  }
}
