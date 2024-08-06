package gdsc.konkuk.platformcore.controller.event;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.konkuk.platformcore.application.event.EventService;

import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.domain.event.entity.Retrospect;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
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

  @Mock Event event;
  @Mock Retrospect retrospect;

  @MockBean private EventService eventService;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp(
      WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    openMocks(this);
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .apply(springSecurity())
            .build();
  }

  @Test
  @DisplayName("이벤트를 등록할 수 있다")
  @WithMockUser
  void should_register_event_when_requested() throws Exception {
    // given
    EventRegisterRequest eventRegisterRequest =
        EventRegisterRequest.builder()
            .title("test title")
            .content("test description")
            .startAt(LocalDateTime.now())
            .endAt(LocalDateTime.now().plusHours(2))
            .build();
    given(eventService.register(any(), any())).willReturn(this.event);

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/events")
                .contentType(MULTIPART_FORM_DATA)
                .formField("imageFiles", "test")
                .formField("registerRequest", objectMapper.writeValueAsString(eventRegisterRequest))
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isCreated())
        .andDo(
            document(
                "registerEvent",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("이벤트를 등록할 수 있다")
                        .tag("events")
                        .responseHeaders(headerWithName("Location").description("등록한 Event URI"))
                        .requestFields(
                            fieldWithPath("title").description("이벤트 제목"),
                            fieldWithPath("description").description("이벤트 설명"),
                            fieldWithPath("thumbnailUrl").description("썸네일 URL"),
                            fieldWithPath("startAt").description("이벤트 시작 시간"),
                            fieldWithPath("endAt").description("이벤트 종료 시간"))
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data").description("null"))
                        .build())));
  }

  @Test
  @DisplayName("이벤트 회고를 조회할 수 있다")
  @WithMockUser
  void should_get_retrospect_when_pass_event_id() throws Exception {
    // given
    given(eventService.getRetrospect(any(Long.class))).willReturn(retrospect);
    given(retrospect.getContent()).willReturn("test contents");

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/events/{eventId}/retrospect", 1L)
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(
            document(
                "getRetrospect",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("이벤트 회고를 조회할 수 있다")
                        .tag("events")
                        .pathParameters(parameterWithName("eventId").description("이벤트 ID"))
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data").description("이벤트 회고"),
                            fieldWithPath("data.content").description("회고 내용"))
                        .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("회고 수정 성공")
  void should_update_retrospect_when_pass_content() throws Exception {
    // given
    RetrospectUpdateRequest retrospectUpdateRequest =
        RetrospectUpdateRequest.builder().content("content").build();
    doNothing().when(eventService).updateRetrospect(any(Long.class), any(String.class));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/api/v1/events/{eventId}/retrospect", 1L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(retrospectUpdateRequest))
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(
            document(
                "updateRetrospect",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("회고를 수정할 수 있다")
                        .tag("events")
                        .pathParameters(parameterWithName("eventId").description("이벤트 ID"))
                        .requestFields(fieldWithPath("content").description("회고 내용"))
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data").description("null"))
                        .build())));
  }
}
