package gdsc.konkuk.platformcore.controller.member;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;

import gdsc.konkuk.platformcore.application.member.MemberRegisterRequest;
import gdsc.konkuk.platformcore.application.member.MemberService;
import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyExistException;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
class MemberControllerTest {

	MockMvc mockMvc;

	@MockBean
	private MemberService memberService;

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
	@DisplayName("새로운 멤버 회원 가입 성공")
	void should_success_when_newMember() throws Exception {
	    //given
		MemberRegisterRequest memberRegisterRequest =
			MemberRegisterRequest.builder()
				.memberId("202011288")
				.password("password")
				.email("example@konkuk.ac.kr")
				.name("홍길동")
				.memberRole(MemberRole.MEMBER)
				.batch(2024)
				.build();

	    given(memberService.register(any(MemberRegisterRequest.class))).willReturn(SuccessResponse.messageOnly());
	    //when
	    mockMvc.perform(
			RestDocumentationRequestBuilders.post("/api/v1/members")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(memberRegisterRequest))
				.with(csrf())

			)

			.andExpect(status().isCreated())
			.andDo(print())
			.andDo(
				document("member/register",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					resource(ResourceSnippetParameters.builder()
						.description("새로운 멤버 회원 가입 성공")
						.tag("member")
						.requestFields(
							fieldWithPath("memberId").description("회원 아이디"),
							fieldWithPath("password").description("비밀번호"),
							fieldWithPath("email").description("이메일"),
							fieldWithPath("name").description("이름"),
							fieldWithPath("memberRole").description("멤버 권한"),
							fieldWithPath("batch").description("배치")
						)
						.responseFields(
							fieldWithPath("success").description(true),
							fieldWithPath("message").description("회원 가입 성공"),
							fieldWithPath("data").description("null")
						)
						.build()
					)
				)
			);
	}

	@Test
	@DisplayName("이미 존재하는 유저 회원 가입 실패")
	void should_fail_when_existingMember() throws Exception {
		//given
		MemberRegisterRequest memberRegisterRequest =
			MemberRegisterRequest.builder()
				.memberId("202011288")
				.password("password")
				.email("example@konkuk.ac.kr")
				.name("홍길동")
				.memberRole(MemberRole.MEMBER)
				.batch(2024)
				.build();

		given(memberService.register(any(MemberRegisterRequest.class))).willThrow(UserAlreadyExistException.class);
		//when
		mockMvc.perform(
				RestDocumentationRequestBuilders.post("/api/v1/members")
					.contentType(APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(memberRegisterRequest))
					.with(csrf())
			)
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
}