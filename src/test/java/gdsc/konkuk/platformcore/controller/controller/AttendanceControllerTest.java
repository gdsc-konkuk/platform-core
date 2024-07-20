package gdsc.konkuk.platformcore.controller.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import gdsc.konkuk.platformcore.application.attendance.AttendanceService;
import gdsc.konkuk.platformcore.application.auth.CustomAuthenticationFailureHandler;
import gdsc.konkuk.platformcore.application.auth.CustomAuthenticationSuccessHandler;
import gdsc.konkuk.platformcore.application.event.EventService;
import gdsc.konkuk.platformcore.controller.attendance.AttendanceController;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participants;
import gdsc.konkuk.platformcore.global.configs.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttendanceController.class)
@ExtendWith({RestDocumentationExtension.class})
@Import({SecurityConfig.class})
public class AttendanceControllerTest {

  private MockMvc mockMvc;

  @MockBean private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
  @MockBean private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
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
  @DisplayName("이벤트에 출석할 수 있다")
  @WithMockUser
  void should_attend_when_pass_event_id_and_member_id() throws Exception {
    // given
    given(attendanceService.attend("test@test.com", 1L))
        .willReturn(new Participants(1L, 1L, 1L, true));

    // when
    ResultActions result =
        mockMvc
            .perform(
                RestDocumentationRequestBuilders.post("/api/v1/attendances/{eventId}", 1)
                    .with(oidcLogin()))
            .andDo(print());

    // then
    result
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
                        .pathParameters(parameterWithName("eventId").description("이벤트 ID"))
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data.id").description("출석 ID"),
                            fieldWithPath("data.eventId").description("이벤트 ID"),
                            fieldWithPath("data.memberId").description("멤버 ID"),
                            fieldWithPath("data.attendance").description("출석 여부"))
                        .build())));
  }
}
