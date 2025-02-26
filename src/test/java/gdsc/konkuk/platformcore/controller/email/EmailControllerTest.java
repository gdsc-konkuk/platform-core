package gdsc.konkuk.platformcore.controller.email;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.konkuk.platformcore.util.annotation.RestDocsTest;
import gdsc.konkuk.platformcore.filter.auth.JwtTokenProvider;
import gdsc.konkuk.platformcore.application.email.EmailService;
import gdsc.konkuk.platformcore.application.email.EmailTaskFacade;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import gdsc.konkuk.platformcore.util.fixture.email.EmailSendRequestFixture;
import gdsc.konkuk.platformcore.util.fixture.email.EmailTaskFixture;
import gdsc.konkuk.platformcore.util.fixture.member.MemberFixture;
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

@RestDocsTest
@SpringBootTest
class EmailControllerTest {

    @MockBean
    EmailTaskFacade emailTaskFacade;
    private MockMvc mockMvc;
    @MockBean
    private EmailService emailService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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
    void should_success_when_send_email() throws Exception {
        //given
        Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
        String jwt = jwtTokenProvider.createToken(member);
        EmailSendRequest request = EmailSendRequestFixture.builder().build().getFixture();
        EmailTask emailTaskToSee = EmailTaskFixture.builder().build().getFixture();
        given(emailTaskFacade.register(any(EmailSendRequest.class))).willReturn(emailTaskToSee);

        //when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/emails")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()));

        //then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(
                        document("email post",
                                preprocessRequest(prettyPrint()),
                                resource(ResourceSnippetParameters.builder()
                                        .tag("email")
                                        .description("이메일 전송을 위한 작업을 등록한다.")
                                        .requestFields(
                                                fieldWithPath("subject").type(JsonFieldType.STRING)
                                                        .description("이메일 제목"),
                                                fieldWithPath("content").type(JsonFieldType.STRING)
                                                        .description("이메일 내용"),
                                                fieldWithPath("receiverInfos[].email").type(
                                                                JsonFieldType.STRING)
                                                        .description("수신자 email"),
                                                fieldWithPath("receiverInfos[].name").type(
                                                        JsonFieldType.STRING).description("수신자 이름"),
                                                fieldWithPath("sendAt").type(JsonFieldType.STRING)
                                                        .description("수정할 이메일 발송 시간"))
                                        .build())));
    }

    @Test
    @DisplayName("이메일 등록 내용 수정")
    void should_success_when_update_emailTask() throws Exception {
        //given
        Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
        String jwt = jwtTokenProvider.createToken(member);
        EmailTask emailTaskToUpdate = EmailTaskFixture.builder()
                .id(1L).build().getFixture();
        EmailSendRequest request = EmailSendRequestFixture.builder().build().getFixture();

        //when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.patch(
                                "/api/v1/emails/{emailId}", 1L)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()));

        //then
        result
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(
                        document("update email task",
                                preprocessRequest(prettyPrint()),
                                resource(ResourceSnippetParameters.builder()
                                        .tag("email")
                                        .description("이메일 전송 작업을 수정한다.")
                                        .requestFields(
                                                fieldWithPath("subject").type(JsonFieldType.STRING)
                                                        .description("수정할 이메일 제목"),
                                                fieldWithPath("content").type(JsonFieldType.STRING)
                                                        .description("수정할 이메일 내용"),
                                                fieldWithPath("receiverInfos[].email").type(
                                                                JsonFieldType.STRING)
                                                        .description("수신자 email"),
                                                fieldWithPath("receiverInfos[].name").type(
                                                        JsonFieldType.STRING).description("수신자 이름"),
                                                fieldWithPath("sendAt").type(JsonFieldType.STRING)
                                                        .description("수정할 이메일 발송 시간"))
                                        .build())));
    }

    @Test
    @DisplayName("이메일 전송 조회 - 모든 이메일 전송 작업 조회")
    void should_success_when_get_all_task() throws Exception {
        //given
        Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
        String jwt = jwtTokenProvider.createToken(member);
        List<EmailTask> emailTasksToSee = List.of(
                EmailTaskFixture.builder().id(1L).build().getFixture(),
                EmailTaskFixture.builder().id(2L).build().getFixture()
        );
        given(emailService.getAllTaskAsList()).willReturn(emailTasksToSee);

        //when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/emails")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(APPLICATION_JSON)
                        .with(csrf()));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("get all email",
                                preprocessRequest(prettyPrint()),
                                resource(ResourceSnippetParameters.builder()
                                        .tag("email")
                                        .description("모든 이메일 전송 작업을 조회한다.")
                                        .responseFields(
                                                fieldWithPath("success").description(true),
                                                fieldWithPath("message").description(
                                                        "이메일 전송 작업 조회 성공"),
                                                fieldWithPath("data").description("이메일 전송 작업 목록"),
                                                fieldWithPath("data.emailTasks").description(
                                                        "이메일 작업 목록"),
                                                fieldWithPath("data.emailTasks[].id").description(
                                                        "이메일 작업의 ID"),
                                                fieldWithPath(
                                                        "data.emailTasks[].subject").description(
                                                        "이메일 제목"),
                                                fieldWithPath(
                                                        "data.emailTasks[].receiverInfos[].email").description(
                                                        "수신자 email"),
                                                fieldWithPath(
                                                        "data.emailTasks[].receiverInfos[].name").description(
                                                        "수신자 이름"),
                                                fieldWithPath(
                                                        "data.emailTasks[].sendAt").description(
                                                        "이메일 발송 예정 시간 (ISO 8601 형식)"),
                                                fieldWithPath(
                                                        "data.emailTasks[].isSent").description(
                                                        "이메일 발송 여부"))
                                        .build())));
    }

    @Test
    @DisplayName("이메일 전송 조회 - 특정 이메일 전송 작업 세부내용 조회")
    void should_success_when_get_specific_task() throws Exception {
        //given
        Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
        String jwt = jwtTokenProvider.createToken(member);
        EmailTask emailTaskToSee = EmailTaskFixture.builder()
                .id(1L).build().getFixture();
        given(emailService.findById(emailTaskToSee.getId()))
                .willReturn(emailTaskToSee);

        //when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.get(
                                "/api/v1/emails/{emailId}", 1L)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(APPLICATION_JSON)
                        .with(csrf()));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("get email detail",
                                preprocessRequest(prettyPrint()),
                                resource(ResourceSnippetParameters.builder()
                                        .tag("email")
                                        .description("특정 이메일 상세정보를 조회한다.")
                                        .pathParameters(
                                                parameterWithName("emailId").description(
                                                        "취소할 이메일 작업 ID"))
                                        .responseFields(
                                                fieldWithPath("success").description(true),
                                                fieldWithPath("message").description(
                                                        "이메일 전송 작업 조회 성공"),
                                                fieldWithPath("data").description("이메일 전송 작업 내용"),
                                                fieldWithPath("data.subject").description("이메일 제목"),
                                                fieldWithPath("data.content").description("이메일 내용"),
                                                fieldWithPath(
                                                        "data.receiverInfos[].email").description(
                                                        "수신자 email"),
                                                fieldWithPath(
                                                        "data.receiverInfos[].name").description(
                                                        "수신자 이름"),
                                                fieldWithPath("data.sendAt").description(
                                                        "이메일 발송 예정 시간 (ISO 8601 형식)"))
                                        .build())));
    }

    @Test
    @DisplayName("등록된 이메일 작업을 취소한다.")
    void should_success_when_cancel_registered_task() throws Exception {
        //given
        Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
        String jwt = jwtTokenProvider.createToken(member);
        EmailTask emailTaskToCancel = EmailTaskFixture.builder()
                .id(1L).build().getFixture();
        willDoNothing().given(emailTaskFacade).cancel(emailTaskToCancel.getId());

        //when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.delete(
                                "/api/v1/emails/{emailId}", 1L)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(APPLICATION_JSON)
                        .with(csrf()));

        //then
        result
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(
                        document("cancel EmailTask",
                                preprocessRequest(prettyPrint()),
                                resource(ResourceSnippetParameters.builder()
                                        .tag("email")
                                        .description("특정 이메일 작업을 취소합니다.")
                                        .pathParameters(
                                                parameterWithName("emailId").description(
                                                        "취소할 이메일 작업 ID"))
                                        .build())));
    }

    @Test
    @DisplayName("선택한 모든 이메일 작업을 취소한다.")
    void should_success_when_cancel_all_tasks() throws Exception {
        //given
        Member member = MemberFixture.builder().role(MemberRole.CORE).build().getFixture();
        String jwt = jwtTokenProvider.createToken(member);
        List<Long> emailIds = List.of(1L, 2L);
        willDoNothing().given(emailTaskFacade).cancelAll(emailIds);

        //when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/emails?emailIds=1&emailIds=2")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(APPLICATION_JSON)
                        .with(csrf()));

        //then
        result
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(
                        document("cancel all EmailTask",
                                preprocessRequest(prettyPrint()),
                                resource(ResourceSnippetParameters.builder()
                                        .tag("email")
                                        .description("선택한 모든 이메일 작업을 취소합니다.")
                                        .build())));
    }
}
