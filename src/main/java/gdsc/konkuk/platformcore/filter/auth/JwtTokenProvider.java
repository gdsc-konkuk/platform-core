package gdsc.konkuk.platformcore.filter.auth;

import gdsc.konkuk.platformcore.domain.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(Member member) {
        Claims claims = Jwts.claims()
                .subject(member.getId().toString())
                .add("studentId", member.getStudentId())
                .add("email", member.getEmail())
                .add("roles", List.of(member.getRole().toString()))
                .build();

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public Authentication getAuthentication(Claims claims) {
        List<? extends GrantedAuthority> authorities =
                ((List<?>) claims.get("roles"))
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.toString()))
                        .toList();

        Map<String, Object> principal = new HashMap<>();
        principal.put("memberId", claims.getSubject());
        principal.put("studentId", claims.get("studentId", String.class));
        principal.put("email", claims.get("email", String.class));
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
