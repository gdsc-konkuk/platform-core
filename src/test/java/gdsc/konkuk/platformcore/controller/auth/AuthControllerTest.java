package gdsc.konkuk.platformcore.controller.auth;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static gdsc.konkuk.platformcore.fixture.member.MemberFixture.getGeneralMemberFixture1;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import gdsc.konkuk.platformcore.annotation.WithCustomUser;
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
  @WithCustomUser
  void loginSuccess() throws Exception {
    // given
    Member memberToLogin = getGeneralMemberFixture1();
    given(memberRepository.findByMemberId(MemberFixture.GENERAL_1_MEMBER_ID))
      .willReturn(Optional.of(memberToLogin));

    // when
    ResultActions result =
      mockMvc.perform(
        RestDocumentationRequestBuilders.multipart("/login")
          .formField("id", MemberFixture.GENERAL_1_MEMBER_ID)
          .formField("password", MemberFixture.GENERAL_PASSWORD)
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
  @DisplayName("사용자 로그인 실패")
  @WithCustomUser
  void loginFail() throws Exception {
    // given
    given(memberRepository.findByMemberId(any())).willReturn(Optional.empty());

    // when
    ResultActions result =
      mockMvc.perform(
        RestDocumentationRequestBuilders.multipart("/login")
          .formField("id", MemberFixture.GENERAL_1_MEMBER_ID)
          .formField("password", MemberFixture.WRONG_PASSWORD)
          .contentType("application/x-www-form-urlencoded")
          .characterEncoding("UTF-8")
          .with(csrf()));

    // then
    result.andDo(print()).andExpect(status().isBadRequest());
  }
}
