package gdsc.konkuk.platformcore;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

import gdsc.konkuk.platformcore.annotation.RestDocsTest;
import gdsc.konkuk.platformcore.controller.SwaggerController;

@DisplayName("스웨거API 테스트 문서화 예시")
@RestDocsTest
@WebMvcTest(SwaggerController.class)
class APIDocumentationTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	void swaggerGetTest() throws Exception {
			mockMvc.perform(
					RestDocumentationRequestBuilders.get("/swagger")
				)
				.andExpect(status().isOk())
				.andDo(MockMvcRestDocumentationWrapper.document("test-get",
					ResourceSnippetParameters.builder()
						.tag("테스트")
						.summary("Get 테스트")
						.description("Get 테스트")
					,
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint())
				));

		}

}
