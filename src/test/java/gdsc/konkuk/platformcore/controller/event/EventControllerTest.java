package gdsc.konkuk.platformcore.controller.event;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import gdsc.konkuk.platformcore.application.event.EventService;
import gdsc.konkuk.platformcore.application.event.EventWithAttendance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.time.LocalDate;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
public class EventControllerTest {

  private MockMvc mockMvc;

  @MockBean
  private EventService eventService;

  @BeforeEach
  void setUp(
    WebApplicationContext webApplicationContext,
    RestDocumentationContextProvider restDocumentation) {
    mockMvc =
      MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(documentationConfiguration(restDocumentation))
        .apply(springSecurity())
        .build();
  }

  @Test
  @DisplayName("특정 달의 이벤트 정보를 조회할 수 있다")
  @WithMockUser
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
        RestDocumentationRequestBuilders.get("/api/v1/events?year=2021&month=1").with(csrf()));

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
              .description("특정 달의 이벤트 정보를 조회할 수 있다")
              .tag("events")
              .queryParameters(
                parameterWithName("year").description("년도"),
                parameterWithName("month").description("월"))
              .responseFields(
                fieldWithPath("success").description("성공 여부"),
                fieldWithPath("message").description("메시지"),
                fieldWithPath("data[].eventId").description("이벤트 ID"),
                fieldWithPath("data[].title").description("이벤트 제목"),
                fieldWithPath("data[].attendanceId").description("출석 ID"),
                fieldWithPath("data[].thumbnailUrl").description("썸네일 URL"),
                fieldWithPath("data[].startAt").description("이벤트 시작 시간"))
              .build())));
  }
}