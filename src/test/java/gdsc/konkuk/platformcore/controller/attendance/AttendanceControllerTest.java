package gdsc.konkuk.platformcore.controller.attendance;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;

import gdsc.konkuk.platformcore.annotation.WithCustomUser;
import gdsc.konkuk.platformcore.application.attendance.AttendanceService;
import gdsc.konkuk.platformcore.application.attendance.dtos.AttendanceStatus;
import gdsc.konkuk.platformcore.application.event.EventService;
import gdsc.konkuk.platformcore.application.event.dtos.EventWithAttendance;
import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceRegisterRequest;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;

import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import gdsc.konkuk.platformcore.fixture.member.MemberFixture;
import java.time.LocalDate;
import java.util.List;
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

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
class AttendanceControllerTest {

  private MockMvc mockMvc;

  @Mock
  Attendance mockAttendance;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AttendanceService attendanceService;
  @MockBean private EventService eventService;

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
  @WithCustomUser(memberId = MemberFixture.ADMIN_MEMBER_ID, role = MemberRole.ADMIN)
  void should_get_events_of_the_month_when_pass_year_month() throws Exception {
    // given
    given(eventService.getEventsOfTheMonthWithAttendance(any(LocalDate.class)))
        .willReturn(
            List.of(
                // TODO: Fixture 정리
                EventWithAttendance.builder().attendanceId(0L).eventId(0L).build(),
                EventWithAttendance.builder().attendanceId(1L).eventId(1L).build(),
                EventWithAttendance.builder().attendanceId(2L).eventId(2L).build()));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/attendances?year=2021&month=1")
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
                            fieldWithPath("data[].eventId").description("이벤트 ID"),
                            fieldWithPath("data[].title").description("이벤트 제목"),
                            fieldWithPath("data[].attendanceId").description("출석 ID"),
                            fieldWithPath("data[].startAt").description("이벤트 시작 시간"))
                        .build())));
  }

  @Test
  @DisplayName("이벤트에 출석할 수 있다")
  @WithCustomUser(memberId = MemberFixture.ADMIN_MEMBER_ID, role = MemberRole.ADMIN)
  void should_attend_when_pass_event_id_and_member_id() throws Exception {
    // given
    Attendance attendance = Attendance.builder()
        .id(1L)
        .eventId(1L)
        .build();
    given(attendanceService.attend(any(), any(), any()))
        .willReturn(new Participant(1L, attendance, true));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                    "/api/v1/attendances/attend/{attendanceId}?qrUuid={uuid}", 1, "uuid")
                .with(oidcLogin()));

    // then
    result
        .andDo(print())
        .andExpect(status().isOk())
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
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data.id").description("참여자 ID"),
                            fieldWithPath("data.attendanceId").description("출석 ID"),
                            fieldWithPath("data.memberId").description("멤버 ID"),
                            fieldWithPath("data.attended").description("출석 여부"))
                        .build())));
  }

  @Test
  @DisplayName("이벤트 출석을 등록할 수 있다")
  @WithCustomUser
  void should_register_attendance_when_pass_event_id() throws Exception {
    // given
    AttendanceRegisterRequest registerRequest =
        AttendanceRegisterRequest.builder().eventId(1L).batch(MemberFixture.BATCH).build();
    given(attendanceService.registerAttendance(any())).willReturn(mockAttendance);
    given(mockAttendance.getActiveQrUuid()).willReturn("uuid");
    given(mockAttendance.getId()).willReturn(1L);

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/attendances")
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
  @WithCustomUser(memberId = MemberFixture.ADMIN_MEMBER_ID, role = MemberRole.ADMIN)
  void should_get_attendance_status_when_pass_attendance_id() throws Exception {
    // given
    given(attendanceService.getAttendanceStatus(any(Long.class)))
        .willReturn(AttendanceStatus.of(1L, 10, 6));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/attendances/{attendanceId}/status", 1)
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
  @WithCustomUser(memberId = MemberFixture.ADMIN_MEMBER_ID, role = MemberRole.ADMIN)
  void should_delete_attendance_when_pass_event_id() throws Exception {
    // given
    doNothing().when(attendanceService).deleteAttendance(any(Long.class));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/attendances/{attendanceId}", 1)
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
  @WithCustomUser(memberId = MemberFixture.ADMIN_MEMBER_ID, role = MemberRole.ADMIN)
  void should_generate_qr_when_pass_attendance_id() throws Exception {
    // given
    given(attendanceService.generateQr(any(Long.class))).willReturn(mockAttendance);
    given(attendanceService.registerAttendance(any())).willReturn(mockAttendance);
    given(mockAttendance.getActiveQrUuid()).willReturn("uuid");
    given(mockAttendance.getId()).willReturn(1L);

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/attendances/{attendanceId}/qr", 1)
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
  @WithCustomUser(memberId = MemberFixture.ADMIN_MEMBER_ID, role = MemberRole.ADMIN)
  void should_expire_qr_when_pass_attendance_id_and_qr_uuid() throws Exception {
    // given
    doNothing().when(attendanceService).expireQr(any(Long.class));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete(
                    "/api/v1/attendances/{attendanceId}/qr", 1)
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
