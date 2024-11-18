package gdsc.konkuk.platformcore.controller.auth;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import gdsc.konkuk.platformcore.annotation.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RestDocsTest
@SpringBootTest
class AuthControllerTest {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentation) {
    mockMvc =
      MockMvcBuilders.webAppContextSetup(this.context)
        .apply(springSecurity())
        .apply(documentationConfiguration(restDocumentation))
        .build();
  }

  @Test
  @DisplayName("사용자 로그인 성공")
  void loginSuccess() throws Exception {
    // given

    // when
    ResultActions result =
      mockMvc.perform(
        RestDocumentationRequestBuilders.multipart("/login/oauth2/authorization/google")
          .characterEncoding("UTF-8")
          .with(csrf()));

    // then
    result
      .andDo(print())
      .andExpect(status().isFound())
      .andDo(
        document(
          "login",
          resource(
            ResourceSnippetParameters.builder()
              .description("사용자 로그인 성공")
              .tag("auth")
              .responseHeaders(headerWithName("Location").description("Google 로그인 페이지 URL"))
              .build())));
  }
}
