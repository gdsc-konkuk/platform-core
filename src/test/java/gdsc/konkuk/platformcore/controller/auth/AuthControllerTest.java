package gdsc.konkuk.platformcore.controller.auth;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import gdsc.konkuk.platformcore.fixture.member.MemberFixture;
import java.util.Optional;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
class AuthControllerTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  PasswordEncoder passwordEncoder;

  @MockBean
  MemberRepository memberRepository;

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
    Member memberToLogin = MemberFixture.builder()
        .memberId("202400000").password(passwordEncoder.encode("password")).build().getFixture();
    given(memberRepository.findByMemberId(memberToLogin.getMemberId()))
      .willReturn(Optional.of(memberToLogin));

    // when
    ResultActions result =
      mockMvc.perform(
        RestDocumentationRequestBuilders.multipart("/login")
          .formField("id", memberToLogin.getMemberId())
          .formField("password", "password")
          .contentType("application/x-www-form-urlencoded")
          .characterEncoding("UTF-8")
          .with(csrf()));

    // then
    result
      .andDo(print())
      .andExpect(status().isOk())
      .andDo(
        document(
          "login",
          resource(
            ResourceSnippetParameters.builder()
              .description("사용자 로그인 성공")
              .tag("auth")
              .build())));
  }

  @Test
  @DisplayName("존재하지 않는 사용자 로그인 실패")
  void loginFail() throws Exception {
    // given
    given(memberRepository.findByMemberId(any())).willReturn(Optional.empty());

    // when
    ResultActions result =
      mockMvc.perform(
        RestDocumentationRequestBuilders.multipart("/login")
          .formField("id", "2024000000")
          .formField("password", "password")
          .contentType("application/x-www-form-urlencoded")
          .characterEncoding("UTF-8")
          .with(csrf()));

    // then
    result.andDo(print()).andExpect(status().isBadRequest());
  }
}
