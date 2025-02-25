package gdsc.konkuk.platformcore.filter.auth;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;
import gdsc.konkuk.platformcore.global.exceptions.GlobalErrorCode;
import gdsc.konkuk.platformcore.global.responses.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws
            IOException {

        CustomErrorCode errorCode =
                request.getContentType().startsWith(APPLICATION_FORM_URLENCODED_VALUE)
                        ? MemberErrorCode.INVALID_USER_INFO : GlobalErrorCode.ARGUMENT_NOT_VALID;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
