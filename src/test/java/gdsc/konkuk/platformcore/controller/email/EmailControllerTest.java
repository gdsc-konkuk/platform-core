package gdsc.konkuk.platformcore.controller.email;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import gdsc.konkuk.platformcore.application.email.EmailScheduleService;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;

import gdsc.konkuk.platformcore.annotation.CustomMockUser;
import gdsc.konkuk.platformcore.annotation.RestDocsTest;
import gdsc.konkuk.platformcore.application.email.EmailService;
import gdsc.konkuk.platformcore.controller.email.dto.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailDetails;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;

@RestDocsTest
@SpringBootTest
class EmailControllerTest {

  private MockMvc mockMvc;
  @MockBean private EmailService emailService;

  @MockBean
  EmailScheduleService emailScheduleService;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp(
      WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    MockitoAnnotations.openMocks(this);
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .apply(documentationConfiguration(restDocumentation))
            .build();
  }

  @Test
  @DisplayName("이메일 전송 작업 등록 성공")
  @CustomMockUser
  void should_success_when_send_email() throws Exception {
    //given
    EmailSendRequest request = EmailSendRequest.builder()
      .subject("예시 이메일 제목")
      .content("Html 문자열")
      .receivers(List.of("ex1@gmail.com", "ex2@naver.com"))
      .sendAt(LocalDateTime.of(2024, 7, 20, 12, 30))
      .build();
    EmailDetails emailDetails = request.toEmailDetails();
    EmailReceivers emailReceivers = request.toEmailReceivers();
    EmailTask mockTask = new EmailTask(1L, emailDetails, emailReceivers, request.getSendAt());

    //when
    when(emailScheduleService.scheduleEmailTask(any())).thenReturn(mockTask);

    ResultActions result = mockMvc.perform(
      RestDocumentationRequestBuilders.post("/api/v1/emails")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .with(csrf()));

    //then
    result
      .andExpect(status().isCreated())
      .andDo(print());

    result
      .andDo(
        document("emails",
          preprocessRequest(prettyPrint()),
          resource(ResourceSnippetParameters.builder()
            .tag("email")
            .description("이메일 전송을 위한 작업을 등록한다.")
            .requestFields(
              fieldWithPath("subject").type(JsonFieldType.STRING).description("이메일 제목"),
              fieldWithPath("content").type(JsonFieldType.STRING).description("이메일 내용"),
              fieldWithPath("receivers").type(JsonFieldType.ARRAY).description("수신자 이메일 목록"),
              fieldWithPath("sendAt").type(JsonFieldType.STRING).description("수정할 이메일 발송 시간")
            )
            .build()))
      );
  }

  @Test
  @DisplayName("이메일 등록 내용 수정")
  @CustomMockUser
  void should_success_when_update_emailTask() throws Exception {
    //given
    EmailSendRequest request = EmailSendRequest.builder()
      .subject("예시 이메일 제목 수정")
      .content("Html 문자열")
      .receivers(List.of("update@gmail.com", "update2@gmail.com", "update3@gmail.com"))
      .sendAt(LocalDateTime.of(2024,7,20,12,30))
      .build();

    //when
    ResultActions result = mockMvc.perform(
      RestDocumentationRequestBuilders.patch("/api/v1/emails/1")
      .contentType(APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request))
      .with(csrf()));

    //then
    result.andExpect(status().isNoContent())
      .andDo(print());

    result.andDo(
      document("emails?emialId={}",
        preprocessRequest(prettyPrint()),
        resource(ResourceSnippetParameters.builder()
          .tag("email")
          .description("이메일 전송 작업을 수정한다.")
          .requestFields(
            fieldWithPath("subject").type(JsonFieldType.STRING).description("수정할 이메일 제목"),
            fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 이메일 내용"),
            fieldWithPath("receivers").type(JsonFieldType.ARRAY).description("수정할 수신자 이메일 목록"),
            fieldWithPath("sendAt").type(JsonFieldType.STRING).description("수정할 이메일 발송 시간")
          ).build()))
    );
  }

  @Test
  @DisplayName("이메일 전송 조회 - 모든 이메일 전송 작업 조회")
  @CustomMockUser
  void should_success_when_get_all_task() throws Exception {
    //given
    EmailDetails emailDetails = new EmailDetails("예시 이메일 제목", "Html 문자열");
    EmailReceivers emailReceivers = new EmailReceivers(List.of("example1@gmail.com", "example2@gmail.com", "example3@gmail.com"));
    EmailTask emailTask = new EmailTask(1L, emailDetails, emailReceivers, LocalDateTime.of(2024, 7, 20, 12, 30));

    //when
    when(emailService.getAllTaskAsList()).thenReturn(List.of(emailTask));
    ResultActions result = mockMvc.perform(
      RestDocumentationRequestBuilders.get("/api/v1/emails")
      .contentType(APPLICATION_JSON)
      .with(csrf()));

    //then
    result.andExpect(status().isOk())
      .andDo(print());

    result.andDo(
      document("emails/",
        preprocessRequest(prettyPrint()),
        resource(ResourceSnippetParameters.builder()
          .tag("email")
          .description("모든 이메일 전송 작업을 조회한다.")
          .responseFields(
            fieldWithPath("success").description(true),
            fieldWithPath("message").description("이메일 전송 작업 조회 성공"),
            fieldWithPath("data").description("이메일 전송 작업 목록"),
            fieldWithPath("data.emailTasks").description("이메일 작업 목록"),
            fieldWithPath("data.emailTasks[].id").description("이메일 작업의 ID (Mock객체에 대해 null일 수 있음.)"),
            fieldWithPath("data.emailTasks[].subject").description("이메일 제목"),
            fieldWithPath("data.emailTasks[].receiver").description("이메일 수신자"),
            fieldWithPath("data.emailTasks[].sendAt").description("이메일 발송 예정 시간 (ISO 8601 형식)"),
            fieldWithPath("data.emailTasks[].isSent").description("이메일 발송 여부")
          ).build()))
    );
  }

  @Test
  @DisplayName("이메일 전송 조회 - 특정 이메일 전송 작업 세부내용 조회")
  @CustomMockUser
  void should_success_when_get_specific_task() throws Exception {
    //given
    EmailDetails emailDetails = new EmailDetails("예시 이메일 제목", "Html 문자열");
    EmailReceivers emailReceivers = new EmailReceivers(List.of("example@gmail.com", "example@naver.com"));
    EmailTask emailTask = new EmailTask(1L, emailDetails, emailReceivers, LocalDateTime.of(2024, 7, 20, 12, 30));

    //when
    when(emailService.getTaskDetails(any())).thenReturn(emailTask);
    ResultActions result = mockMvc.perform(
      RestDocumentationRequestBuilders.get("/api/v1/emails/{emailId}", 1)
        .contentType(APPLICATION_JSON)
        .with(csrf()));

    //then
    result.andExpect(status().isOk())
      .andDo(print());

    result.andDo(
      document("emails/{emailId}",
        preprocessRequest(prettyPrint()),
        resource(ResourceSnippetParameters.builder()
          .tag("email")
          .description("특정 이메일 상세정보를 조회한다.")
          .responseFields(
            fieldWithPath("success").description(true),
            fieldWithPath("message").description("이메일 전송 작업 조회 성공"),
            fieldWithPath("data").description("이메일 전송 작업 내용"),
            fieldWithPath("data.subject").description("이메일 제목"),
            fieldWithPath("data.content").description("이메일 내용"),
            fieldWithPath("data.receivers").description("이메일 수신자"),
            fieldWithPath("data.sendAt").description("이메일 발송 예정 시간 (ISO 8601 형식)")
          )
          .pathParameters(
            parameterWithName("emailId").description("취소할 이메일 작업 ID")
          ).build()))
    );
  }

  @Test
  @DisplayName("등록된 이메일 작업을 취소한다.")
  void should_success_when_cancel_registered_task() throws Exception {
    //given
    doNothing().when(emailScheduleService).cancelEmailTask(any());

    //when
    ResultActions result = mockMvc.perform(
      RestDocumentationRequestBuilders.delete("/api/v1/emails/{emailId}", 1)
        .contentType(APPLICATION_JSON)
        .with(csrf()));

    //then
    result.andExpect(status().isNoContent())
      .andDo(print());

    result.andDo(
      document("cancel EmailTask",
        preprocessRequest(prettyPrint()),
        resource(ResourceSnippetParameters.builder()
          .tag("email")
          .description("특정 이메일 작업을 취소합니다.")
          .pathParameters(
            parameterWithName("emailId").description("취소할 이메일 작업 ID")
          ).build()))
    );
  }
}