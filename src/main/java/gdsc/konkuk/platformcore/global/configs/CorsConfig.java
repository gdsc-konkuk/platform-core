package gdsc.konkuk.platformcore.global.configs;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.allowedOrigins;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public UrlBasedCorsConfigurationSource getConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(
                Arrays.asList("Origin", "X-Requested-With", "Content-Type", "Accept",
                        "Authorization", "Location", "Range", "Cache-Control", "User-Agent",
                        "DNT"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
