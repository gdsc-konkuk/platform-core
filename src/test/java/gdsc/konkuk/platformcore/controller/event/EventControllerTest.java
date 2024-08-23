package gdsc.konkuk.platformcore.controller.event;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static gdsc.konkuk.platformcore.fixture.event.EventBriefResponseFixture.getEventBriefResponseFixture;
import static gdsc.konkuk.platformcore.fixture.event.EventFixture.getEventFixture1;
import static gdsc.konkuk.platformcore.fixture.event.EventRegisterRequestFixture.getEventFixture1RegisterRequest;
import static gdsc.konkuk.platformcore.fixture.event.EventUpdateRequestFixture.getEventFixture1UpdateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
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
import gdsc.konkuk.platformcore.annotation.WithCustomUser;
import gdsc.konkuk.platformcore.application.event.EventService;
import gdsc.konkuk.platformcore.controller.event.dtos.EventDetailResponse;
import gdsc.konkuk.platformcore.controller.event.dtos.EventRegisterRequest;
import gdsc.konkuk.platformcore.controller.event.dtos.EventUpdateRequest;
import gdsc.konkuk.platformcore.controller.event.dtos.RetrospectUpdateRequest;
import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import gdsc.konkuk.platformcore.fixture.member.MemberFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
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
  @WithCustomUser(memberId = MemberFixture.ADMIN_MEMBER_ID, role = MemberRole.ADMIN)
  void should_get_all_events_when_request() throws Exception {
    // given
    given(eventService.getAllBriefs()).willReturn(getEventBriefResponseFixture());

    // when
    ResultActions result =
        mockMvc.perform(RestDocumentationRequestBuilders
            .get("/api/v1/events")
            .with(csrf()));

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
                            fieldWithPath("data.eventBriefs[].thumbnail").description("썸네일 URL").optional())
                        .build())));
  }

  @Test
  @DisplayName("특정 이벤트를 상세 조회할 수 있다")
  @WithCustomUser(memberId = MemberFixture.ADMIN_MEMBER_ID, role = MemberRole.ADMIN)
  void should_get_event_detail_when_pass_event_id() throws Exception {
    // given
    Event eventToSee = getEventFixture1();
    given(eventService.getEvent(eventToSee.getId())).willReturn(EventDetailResponse.fromEntity(eventToSee));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("/api/v1/events/{eventId}", eventToSee.getId())
                .with(csrf()));

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
  @WithCustomUser(memberId = MemberFixture.ADMIN_MEMBER_ID, role = MemberRole.ADMIN)
  void should_register_event_when_requested() throws Exception {
    // given
    EventRegisterRequest eventRegisterRequest = getEventFixture1RegisterRequest();
    given(eventService.register(any(EventRegisterRequest.class), any(List.class)))
        .willReturn(getEventFixture1());

    MockMultipartFile mockImages =
        new MockMultipartFile("images", "test.jpg", "image/jpeg", "test".getBytes());
    MockMultipartFile mockDetail =
        new MockMultipartFile(
            "detail",
            "",
            "application/json",
            objectMapper.writeValueAsString(eventRegisterRequest).getBytes());
    String mockRequestBodyForDocument =
        objectMapper.writeValueAsString(
            new Object() {
              public final Object detail = eventRegisterRequest;
              public final List<MultipartFile> images = List.of();
            });

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders
                .multipart("/api/v1/events")
                .file(mockImages)
                .file(mockDetail)
                .content(mockRequestBodyForDocument)
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
  @WithCustomUser(memberId = MemberFixture.ADMIN_MEMBER_ID, role = MemberRole.ADMIN)
  void should_update_event_when_requested() throws Exception {
    // given
    EventUpdateRequest eventUpdateRequest = getEventFixture1UpdateRequest();
    Event eventToUpdate = getEventFixture1();
    willDoNothing().given(eventService)
//        .update(eventToUpdate.getId(), any(EventUpdateRequest.class), any(List.class));
        .update(any(Long.class), any(EventUpdateRequest.class), any(List.class));

    MockMultipartFile mockImages =
        new MockMultipartFile("new-images", "test.jpg", "image/jpeg", "test".getBytes());
    MockMultipartFile mockDetail =
        new MockMultipartFile(
            "detail",
            "",
            "application/json",
            objectMapper.writeValueAsString(eventUpdateRequest).getBytes());
    String mockRequestBodyForDocument =
        objectMapper.writeValueAsString(
            new Object() {
              public final Object detail = eventUpdateRequest;
              public final List<MultipartFile> newImages = List.of();
            });

    // when
    MockMultipartHttpServletRequestBuilder putMultipartRestDocumentationRequestBuilder =
        RestDocumentationRequestBuilders
            .multipart("/api/v1/events/{eventId}", eventToUpdate.getId());
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
                .content(mockRequestBodyForDocument)
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
  @DisplayName("회고 수정 성공")
  @WithCustomUser(memberId = MemberFixture.ADMIN_MEMBER_ID, role = MemberRole.ADMIN)
  void should_update_retrospect_when_pass_content() throws Exception {
    // given
    Event eventToUpdateRetrospect = getEventFixture1();
    RetrospectUpdateRequest retrospectUpdateRequest = new RetrospectUpdateRequest("content");
    willDoNothing().given(eventService)
        .updateRetrospect(eventToUpdateRetrospect.getId(), "content");

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders
                .patch(
                    "/api/v1/events/{eventId}/retrospect",
                    eventToUpdateRetrospect.getId())
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

  @Test
  @DisplayName("이벤트 삭제 성공")
  @WithCustomUser(memberId = MemberFixture.ADMIN_MEMBER_ID, role = MemberRole.ADMIN)
  void should_delete_event_when_pass_event_id() throws Exception {
    // given
    Event eventToDelete = getEventFixture1();
    willDoNothing().given(eventService).delete(eventToDelete.getId());

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders
                .delete(
                    "/api/v1/events/{eventId}",
                    eventToDelete.getId())
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "deleteEvent",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("이벤트를 삭제할 수 있다")
                        .tag("events")
                        .pathParameters(parameterWithName("eventId").description("이벤트 ID"))
                        .build())));
  }
}
