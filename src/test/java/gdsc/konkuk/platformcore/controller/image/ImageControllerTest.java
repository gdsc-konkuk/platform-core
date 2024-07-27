package gdsc.konkuk.platformcore.controller.image;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import gdsc.konkuk.platformcore.application.image.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URL;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
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
public class ImageControllerTest {

  private MockMvc mockMvc;

  @MockBean ImageService imageService;

  @BeforeEach
  void setUp(
      WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .apply(springSecurity())
            .build();
  }

  @Test
  @DisplayName("이미지 업로드 URL 생성할 수 있다")
  void should_create_image_upload_url_when_pass_file_name() throws Exception {
    // given
    given(imageService.getUploadUrl("test.jpg"))
        .willReturn(new URL("https://aws.s3.com/pre/signed/url/to/upload"));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(
                    "/api/v1/images/{fileName}/upload-url", "test.jpg")
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isCreated())
        .andDo(
            document(
                "image/upload-url",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("이미지 업로드 URL 생성 성공")
                        .tag("images")
                        .responseHeaders(
                            headerWithName("Location").description("생성한 이미지 upload url"))
                        .responseFields(
                            fieldWithPath("success").description(true),
                            fieldWithPath("message").description("이미지 upload url 생성 성공"),
                            fieldWithPath("data").description("null"))
                        .build())));
  }

  @Test
  @DisplayName("이미지 다운로드 URL 생성할 수 있다")
  void should_create_image_download_url_when_pass_file_name() throws Exception {
    // given
    given(imageService.getDownloadUrl("test.jpg"))
        .willReturn(new URL("https://aws.s3.com/pre/signed/url/to/download"));

    // when
    ResultActions result =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(
                    "/api/v1/images/{fileName}/download-url", "test.jpg")
                .with(csrf()));

    // then
    result
        .andDo(print())
        .andExpect(status().isCreated())
        .andDo(
            document(
                "image/download-url",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .description("이미지 다운로드 URL 생성 성공")
                        .tag("images")
                        .responseHeaders(
                            headerWithName("Location").description("생성한 이미지 download url"))
                        .responseFields(
                            fieldWithPath("success").description(true),
                            fieldWithPath("message").description("이미지 download url 생성 성공"),
                            fieldWithPath("data").description("null"))
                        .build())));
  }
}
