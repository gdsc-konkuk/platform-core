package gdsc.konkuk.platformcore.controller.attendance;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;

import gdsc.konkuk.platformcore.annotation.RestDocsTest;
import gdsc.konkuk.platformcore.application.attendance.AttendanceService;
import gdsc.konkuk.platformcore.application.attendance.dtos.AttendanceStatus;
import gdsc.konkuk.platformcore.application.auth.JwtTokenProvider;
import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceRegisterRequest;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;

import gdsc.konkuk.platformcore.domain.attendance.entity.AttendanceType;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import gdsc.konkuk.platformcore.fixture.attendance.AttendanceFixture;
import gdsc.konkuk.platformcore.fixture.attendance.AttendanceRegisterRequestFixture;
import gdsc.konkuk.platformcore.fixture.attendance.ParticipantFixture;
import gdsc.konkuk.platformcore.fixture.member.MemberFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RestDocsTest
@SpringBootTest
class AttendanceControllerTest {

  private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtTokenProvider jwtTokenProvider;

  @MockBean private AttendanceService attendanceService;

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
  @DisplayName("특정 달의 출석 정보를 조회할 수 있다")
  void should_get_events_of_the_month_when_pass_year_month() throws Exception {
    // given
    Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
    String jwt = jwtTokenProvider.createToken(member);
    given(attendanceService.getAllByPeriod(LocalDate.of(2024, 7, 1)))
        .willReturn(
            List.of(
                AttendanceFixture.builder()
                    .id(1L)
                    .attendanceTime(LocalDateTime.of(2024, 7, 1, 15, 30))
                    .build().getFixture(),
                AttendanceFixture.builder()
                    .id(2L)
                    .attendanceTime(LocalDateTime.of(2024, 7, 15, 15, 30))
                    .build().getFixture(),
                AttendanceFixture.builder()
                    .id(3L)
                    .attendanceTime(LocalDateTime.of(2024, 7, 21, 15, 30))
                    .build().getFixture()));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/attendances")
                .header("Authorization", "Bearer " + jwt)
                .queryParam("year", "2024")
                .queryParam("month", "07")
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(
            document(
                "eventsByMonth",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("특정 달의 출석 정보를 조회할 수 있다")
                        .tag("attendance")
                        .queryParameters(
                            parameterWithName("year").description("년도"),
                            parameterWithName("month").description("월"))
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data[].attendanceId").description("출석 ID").optional(),
                            fieldWithPath("data[].title").description("출석 제목"),
                            fieldWithPath("data[].attendanceTime").description("출석 시간"))
                        .build())));
  }

  @Test
  @DisplayName("이벤트에 출석할 수 있다")
  void should_attend_when_pass_event_id_and_member_id() throws Exception {
    // given
    Member memberToAttend = MemberFixture.builder()
        .email("ex@gmail.com").build().getFixture();
    String jwt = jwtTokenProvider.createToken(memberToAttend);
    Attendance attendanceToAttend = AttendanceFixture.builder()
        .id(1L).activeQrUuid("uuid").build().getFixture();
    given(attendanceService.attend(memberToAttend.getId(), attendanceToAttend.getId(), attendanceToAttend.getActiveQrUuid()))
        .willReturn(ParticipantFixture.builder()
            .attendanceType(AttendanceType.ATTEND)
            .memberId(memberToAttend.getId())
            .attendance(attendanceToAttend)
            .build().getFixture());

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                    "/api/v1/attendances/attend/{attendanceId}", 1L)
                .header("Authorization", "Bearer " + jwt)
                .queryParam("qrUuid", "uuid")
                .with(oidcLogin()
                    .idToken(token -> token.claim("email", "ex@gmail.com"))));

    // then
    result
        .andDo(print())
        .andExpect(status().isTemporaryRedirect())
        .andDo(
            document(
                "attend",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("이벤트에 출석할 수 있다")
                        .tag("attendance")
                        .pathParameters(parameterWithName("attendanceId").description("출석 ID"))
                        .queryParameters(parameterWithName("qrUuid").description("QR 코드 UUID"))
                        .responseHeaders(headerWithName("Location").description("출석 결과 페이지"))
                        .build())));
  }

  @Test
  @DisplayName("이벤트 출석을 등록할 수 있다")
  void should_register_attendance_when_pass_event_id() throws Exception {
    // given
    Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
    String jwt = jwtTokenProvider.createToken(member);
    AttendanceRegisterRequest registerRequest = AttendanceRegisterRequestFixture.builder().build().getFixture();
    Attendance attendanceToRegister = AttendanceFixture.builder()
            .title(registerRequest.getTitle()).build().getFixture();
    given(attendanceService.registerAttendance(registerRequest.getTitle(), registerRequest.getBatch()))
            .willReturn(attendanceToRegister);

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/attendances")
                .header("Authorization", "Bearer " + jwt)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isCreated())
        .andDo(
            document(
                "registerAttendance",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("이벤트 출석을 등록할 수 있다")
                        .tag("attendance")
                        .responseHeaders(headerWithName("Location").description("등록한 출석"))
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data.attendanceId").description("출석 ID"),
                            fieldWithPath("data.attendUrl").description("출석 URL"))
                        .build())));
  }

  @Test
  @DisplayName("출석 현황을 조회할 수 있다")
  void should_get_attendance_status_when_pass_attendance_id() throws Exception {
    // given
    Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
    String jwt = jwtTokenProvider.createToken(member);
    Attendance attendanceToGetStatus = AttendanceFixture.builder()
        .id(1L).build().getFixture();
    given(attendanceService.getAttendanceStatus(attendanceToGetStatus.getId()))
        .willReturn(AttendanceStatus.of(attendanceToGetStatus.getId(), 10, 6));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                    "/api/v1/attendances/{attendanceId}/status",
                    1L)
                .header("Authorization", "Bearer " + jwt)
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(
            document(
                "getAttendanceStatus",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("출석 현황을 조회할 수 있다")
                        .tag("attendance")
                        .pathParameters(parameterWithName("attendanceId").description("출석 ID"))
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data.attendanceId").description("출석 ID"),
                            fieldWithPath("data.total").description("전체 인원"),
                            fieldWithPath("data.attended").description("출석 인원"))
                        .build())));
  }

  @Test
  @DisplayName("이벤트 출석을 삭제할 수 있다")
  void should_delete_attendance_when_pass_event_id() throws Exception {
    // given
    Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
    String jwt = jwtTokenProvider.createToken(member);
    Attendance attendanceToDelete = AttendanceFixture.builder()
        .id(1L).build().getFixture();
    willDoNothing().given(attendanceService).deleteAttendance(attendanceToDelete.getId());

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete(
                "/api/v1/attendances/{attendanceId}", 1L)
                .header("Authorization", "Bearer " + jwt)
                .contentType(APPLICATION_JSON)
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "deleteAttendance",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("이벤트 출석을 삭제할 수 있다")
                        .tag("attendance")
                        .pathParameters(parameterWithName("attendanceId").description("출석 ID"))
                        .build())));
  }

  @Test
  @DisplayName("QR 코드를 생성할 수 있다")
  void should_generate_qr_when_pass_attendance_id() throws Exception {
    // given
    Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
    String jwt = jwtTokenProvider.createToken(member);
    Attendance attendanceToActive = AttendanceFixture.builder()
        .id(1L).build().getFixture();
    given(attendanceService.generateQr(attendanceToActive.getId()))
        .willAnswer(invocation -> {
          attendanceToActive.generateQr();
          return attendanceToActive;
        });

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(
                    "/api/v1/attendances/{attendanceId}/qr", 1L)
                .header("Authorization", "Bearer " + jwt)
                .contentType(APPLICATION_JSON)
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isCreated())
        .andDo(
            document(
                "generateQr",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("QR 코드를 생성할 수 있다")
                        .tag("attendance")
                        .pathParameters(parameterWithName("attendanceId").description("출석 ID"))
                        .responseHeaders(headerWithName("Location").description("출석 URL"))
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data.attendanceId").description("출석 ID"),
                            fieldWithPath("data.attendUrl").description("출석 URL"))
                        .build())));
  }

  @Test
  @DisplayName("QR 코드를 만료시킬 수 있다")
  void should_expire_qr_when_pass_attendance_id_and_qr_uuid() throws Exception {
    // given
    Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
    String jwt = jwtTokenProvider.createToken(member);
    Attendance attendanceToInactive = AttendanceFixture.builder()
        .id(1L).build().getFixture();
    willDoNothing().given(attendanceService).expireQr(attendanceToInactive.getId());

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete(
                    "/api/v1/attendances/{attendanceId}/qr", 1L)
                .header("Authorization", "Bearer " + jwt)
                .contentType(APPLICATION_JSON)
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "expireQr",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("QR 코드를 만료시킬 수 있다")
                        .tag("attendance")
                        .pathParameters(parameterWithName("attendanceId").description("출석 ID"))
                        .build())));
  }
}
