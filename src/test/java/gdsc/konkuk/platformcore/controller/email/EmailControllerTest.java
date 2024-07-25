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

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

// 1. 이메일 전송 성공 테스트
// 2. 이메일 전송 실패 테스트
// 2-1 이메일 전송 실패 - 잘못된 형식의 수신자 이메일
// 2-2 이메일 전송 실패 - 잘못된 형식의 content
@RestDocsTest
@WebMvcTest(EmailController.class)
class EmailControllerTest {

  private MockMvc mockMvc;

  @MockBean
  private EmailService emailService;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders
      .webAppContextSetup(webApplicationContext)
      .apply(springSecurity())
      .apply(documentationConfiguration(restDocumentation))
      .build();
  }

  @Test
  @DisplayName("이메일 전송 성공")
  @CustomMockUser
  void should_success_when_send_email() throws Exception {
    //given
    EmailSendRequest request = EmailSendRequest.builder()
      .subject("예시 이메일 제목")
      .content("Html 문자열")
      .receivers(List.of("ex1@gmail.com", "ex2@naver.com"))
      .build();

    when(emailService.process(request)).thenReturn(1L);

    ResultActions result = mockMvc.perform(
      RestDocumentationRequestBuilders.post("/api/v1/emails")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .with(csrf()));

    result
      .andExpect(status().isCreated())
      .andDo(print());

    result
      .andDo(
        document("emails",
          preprocessRequest(prettyPrint()),
          resource(ResourceSnippetParameters.builder()
            .requestFields(
              fieldWithPath("subject").type(JsonFieldType.STRING).description("이메일 제목"),
              fieldWithPath("content").type(JsonFieldType.STRING).description("이메일 내용"),
              fieldWithPath("receivers").type(JsonFieldType.ARRAY).description("수신자 이메일 목록")
            )
            .responseFields(
              fieldWithPath("success").description(true),
              fieldWithPath("message").description("이메일 전송 성공"),
              fieldWithPath("data").description("null")
            )
            .build()))
      );
  }

}