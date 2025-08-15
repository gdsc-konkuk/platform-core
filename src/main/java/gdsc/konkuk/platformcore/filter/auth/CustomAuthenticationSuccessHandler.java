package gdsc.konkuk.platformcore.filter.auth;

import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import gdsc.konkuk.platformcore.global.consts.SPAConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final SPAConstants spaConstants;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        Member member = memberRepository.findByEmail(oidcUser.getEmail())
                .orElseThrow(() -> UserNotFoundException.of(MemberErrorCode.USER_NOT_FOUND));
        String token = jwtTokenProvider.createToken(member);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // TODO: Admin이 아닌 여러 클라이언트를 사용하게 될 경우, authorization code를 통한 POST 로그인으로 변경 필요
        response.sendRedirect( spaConstants.getSpaAdminLoginRedirectUrl()+ "#" + token);
    }
}
