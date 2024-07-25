package gdsc.konkuk.platformcore.controller.attendance;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.konkuk.platformcore.application.attendance.AttendanceService;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
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
public class AttendanceControllerTest {

  private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

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
  @DisplayName("이벤트에 출석할 수 있다")
  @WithMockUser
  void should_attend_when_pass_event_id_and_member_id() throws Exception {
    // given
    given(attendanceService.attend(any(), any(), any()))
        .willReturn(new Participant(1L, 1L, 1L, true));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                    "/api/v1/attendances/{attendanceId}?qrUuid={uuid}", 1, "uuid")
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
                            fieldWithPath("data.attendance").description("출석 여부"))
                        .build())));
  }

  @Test
  @DisplayName("이벤트 출석을 등록할 수 있다")
  @WithMockUser
  void should_register_attendance_when_pass_event_id() throws Exception {
    // given
    AttendanceRegisterRequest registerRequest =
        AttendanceRegisterRequest.builder().eventId(1L).batch("24-25").build();
    given(attendanceService.registerAttendance(any())).willReturn(1L);

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
                            fieldWithPath("data").description("null"))
                        .build())));
  }

  @Test
  @DisplayName("이벤트 출석을 삭제할 수 있다")
  @WithMockUser
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
  @WithMockUser
  void should_generate_qr_when_pass_attendance_id() throws Exception {
    // given
    given(attendanceService.generateQr(any(Long.class))).willReturn("uuid");

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
                        .responseHeaders(headerWithName("Location").description("QR 코드 URL"))
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data").description("null"))
                        .build())));
  }

  @Test
  @DisplayName("QR 코드를 만료시킬 수 있다")
  @WithMockUser
  void should_expire_qr_when_pass_attendance_id_and_qr_uuid() throws Exception {
    // given
    doNothing().when(attendanceService).expireQr(any(Long.class), any(String.class));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete(
                    "/api/v1/attendances/{attendanceId}/qr?qrUuid={uuid}", 1, "uuid")
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
                        .queryParameters(parameterWithName("qrUuid").description("QR 코드 UUID"))
                        .pathParameters(parameterWithName("attendanceId").description("출석 ID"))
                        .build())));
  }
}
