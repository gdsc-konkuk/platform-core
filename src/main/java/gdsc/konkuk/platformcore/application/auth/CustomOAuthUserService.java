package gdsc.konkuk.platformcore.application.auth;

import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuthUserService extends OidcUserService {

    private final MemberRepository memberRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OidcUser oidcUser = super.loadUser(userRequest);
            return processOAuth2User(userRequest, oidcUser);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("processing_error", "Failed to process user info", null));
        }
    }

    private OidcUser processOAuth2User(OidcUserRequest _userRequest, OidcUser oidcUser) {
        String email = oidcUser.getEmail();
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> UserNotFoundException.of(MemberErrorCode.USER_NOT_FOUND));

        return new DefaultOidcUser(
            List.of(new SimpleGrantedAuthority(member.getRole().toString())),
            oidcUser.getIdToken(),
            oidcUser.getUserInfo());
    }
}
