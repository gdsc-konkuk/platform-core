package gdsc.konkuk.platformcore.controller.event;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.konkuk.platformcore.application.event.dtos.EventBrief;
import gdsc.konkuk.platformcore.application.event.EventService;
import gdsc.konkuk.platformcore.controller.event.dtos.EventBriefResponse;
import gdsc.konkuk.platformcore.controller.event.dtos.EventDetailResponse;
import gdsc.konkuk.platformcore.controller.event.dtos.EventRegisterRequest;
import gdsc.konkuk.platformcore.controller.event.dtos.EventUpdateRequest;
import gdsc.konkuk.platformcore.controller.event.dtos.RetrospectUpdateRequest;
import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.domain.event.entity.Retrospect;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

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
  @DisplayName("모든 이벤트를 간략 조회할 수 있다")
  @WithMockUser
  void should_get_all_events_when_request() throws Exception {
    // given
    given(eventService.getAllBriefs())
        .willReturn(
            EventBriefResponse.builder()
                .eventBriefs(
                    List.of(
                        EventBrief.builder()
                            .id(1L)
                            .title("test event1")
                            .content("test event content")
                            .startAt(LocalDateTime.now())
                            .thumbnail(new URL("https://foo.com/bar/baz.jpg"))
                            .build(),
                        EventBrief.builder()
                            .id(2L)
                            .title("test event2")
                            .content("test event content 2")
                            .startAt(LocalDateTime.now())
                            .thumbnail(new URL("https://foo.com/bar/baz.jpg"))
                            .build(),
                        EventBrief.builder()
                            .id(3L)
                            .title("test event3")
                            .content("test event content")
                            .startAt(LocalDateTime.now())
                            .thumbnail(new URL("https://foo.com/bar/baz.jpg"))
                            .build()))
                .build());

    // when
    ResultActions result =
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/events").with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(
            document(
                "getAllEvents",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("모든 이벤트를 조회할 수 있다")
                        .tag("events")
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data.eventBriefs[].id").description("이벤트 ID"),
                            fieldWithPath("data.eventBriefs[].title").description("이벤트 제목"),
                            fieldWithPath("data.eventBriefs[].content").description("이벤트 내용"),
                            fieldWithPath("data.eventBriefs[].startAt").description("이벤트 시작 시간"),
                            fieldWithPath("data.eventBriefs[].thumbnail").description("썸네일 URL"))
                        .build())));
  }

  @Test
  @DisplayName("특정 이벤트를 상세 조회할 수 있다")
  @WithMockUser
  void should_get_event_detail_when_pass_event_id() throws Exception {
    // given
    given(eventService.getEvent(any(Long.class)))
        .willReturn(
            EventDetailResponse.builder()
                .id(1L)
                .title("test title")
                .content("test content")
                .location("test location")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusHours(2))
                .images(
                    List.of(
                        new URL("https://foo.com/bar/baz1.jpg"),
                        new URL("https://foo.com/bar/baz2.jpg"),
                        new URL("https://foo.com/bar/baz3.png")))
                .build());

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/events/{eventId}", 1L).with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(
            document(
                "getEvent",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("특정 이벤트를 상세 조회할 수 있다")
                        .tag("events")
                        .pathParameters(parameterWithName("eventId").description("이벤트 ID"))
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data.id").description("이벤트 ID"),
                            fieldWithPath("data.title").description("이벤트 제목"),
                            fieldWithPath("data.content").description("이벤트 내용"),
                            fieldWithPath("data.location").description("이벤트 장소"),
                            fieldWithPath("data.startAt").description("이벤트 시작 시간"),
                            fieldWithPath("data.endAt").description("이벤트 종료 시간"),
                            fieldWithPath("data.images[]").description("이미지 URL"),
                            fieldWithPath("data.retrospect").description("회고 내용"))
                        .build())));
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
            .location("test location")
            .startAt(LocalDateTime.now())
            .endAt(LocalDateTime.now().plusHours(2))
            .build();
    MockMultipartFile mockImages =
        new MockMultipartFile("images", "test.jpg", "image/jpeg", "test".getBytes());
    MockMultipartFile mockDetail =
        new MockMultipartFile(
            "detail",
            "",
            "application/json",
            objectMapper.writeValueAsString(eventRegisterRequest).getBytes());
    given(eventService.register(any(), any())).willReturn(this.event);

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.multipart("/api/v1/events")
                .file(mockImages)
                .file(mockDetail)
                .content(
                    objectMapper.writeValueAsString(
                        new Object() {
                          public Object detail = eventRegisterRequest;
                          public List<MultipartFile> images = List.of();
                        }))
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
                            fieldWithPath("detail")
                                .description(
                                    """
                                    이벤트 정보
                                    - title(String): 이벤트 제목
                                    - content(String): 이벤트 내용
                                    - location(String): 이벤트 장소
                                    - startAt(DateTime): 이벤트 시작 시간
                                    - endAt(DateTime): 이벤트 종료 시간
                                    """),
                            fieldWithPath("detail.title").description("이벤트 제목"),
                            fieldWithPath("detail.content").description("이벤트 내용"),
                            fieldWithPath("detail.location").description("이벤트 장소"),
                            fieldWithPath("detail.startAt").description("이벤트 시작 시간"),
                            fieldWithPath("detail.endAt").description("이벤트 종료 시간"),
                            fieldWithPath("images[]").description("이미지 파일 목록 (여러개)").optional())
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data").description("null"))
                        .build())));
  }

  @Test
  @DisplayName("이벤트를 수정할 수 있다")
  @WithMockUser
  void should_update_event_when_requested() throws Exception {
    // given
    EventUpdateRequest eventUpdateRequest =
        EventUpdateRequest.builder()
            .title("test title")
            .content("test description")
            .startAt(LocalDateTime.now())
            .endAt(LocalDateTime.now().plusHours(2))
            .eventImagesToDelete(
                List.of(new URL("https://s3.com/key1"), new URL("https://s3.com/key2")))
            .build();
    MockMultipartFile mockImages =
        new MockMultipartFile("new-images", "test.jpg", "image/jpeg", "test".getBytes());
    MockMultipartFile mockDetail =
        new MockMultipartFile(
            "detail",
            "",
            "application/json",
            objectMapper.writeValueAsString(eventUpdateRequest).getBytes());
    doNothing().when(eventService).update(any(Long.class), any(EventUpdateRequest.class), any());

    // when
    MockMultipartHttpServletRequestBuilder putMultipartRestDocumentationRequestBuilder =
        RestDocumentationRequestBuilders.multipart("/api/v1/events/{eventId}", 1L);
    putMultipartRestDocumentationRequestBuilder.with(
        request -> {
          request.setMethod("PATCH");
          return request;
        });
    ResultActions result =
        mockMvc.perform(
            putMultipartRestDocumentationRequestBuilder
                .file(mockImages)
                .file(mockDetail)
                .content(
                    objectMapper.writeValueAsString(
                        new Object() {
                          public Object detail = eventUpdateRequest;
                          public List<MultipartFile> newImages = List.of();
                        }))
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(
            document(
                "updateEvent",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("이벤트를 수정할 수 있다")
                        .tag("events")
                        .pathParameters(parameterWithName("eventId").description("이벤트 ID"))
                        .requestFields(
                            fieldWithPath("newImages[]")
                                .description(
                                    """
                                    **주의! form field 이름은 `new-images`입니다.**
                                    새로 추가할 이미지 파일 목록 (여러개)
                                    """)
                                .optional(),
                            fieldWithPath("detail")
                                .description(
                                    """
                                    이벤트 정보
                                    - title(String?): 이벤트 제목
                                    - content(String?): 이벤트 내용
                                    - location(String?): 이벤트 장소
                                    - startAt(DateTime?): 이벤트 시작 시간
                                    - endAt(DateTime?): 이벤트 종료 시간
                                    - eventImageKeysToDelete(String[]?): 삭제할 이미지 URL 목록
                                    """),
                            fieldWithPath("detail.title").description("이벤트 제목").optional(),
                            fieldWithPath("detail.content").description("이벤트 내용").optional(),
                            fieldWithPath("detail.location").description("이벤트 장소").optional(),
                            fieldWithPath("detail.startAt").description("이벤트 시작 시간").optional(),
                            fieldWithPath("detail.endAt").description("이벤트 종료 시간").optional(),
                            fieldWithPath("detail.eventImagesToDelete[]")
                                .description("삭제할 이미지 Key 목록").optional())
                        .responseFields(
                            fieldWithPath("success").description("성공 여부"),
                            fieldWithPath("message").description("메시지"),
                            fieldWithPath("data").description("null"))
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
