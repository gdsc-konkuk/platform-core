package gdsc.konkuk.platformcore.global.configs;


import java.util.Arrays;

import gdsc.konkuk.platformcore.global.consts.PlatformConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final PlatformConstants platformConstants;

    @Bean
    public UrlBasedCorsConfigurationSource getConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(platformConstants.getAllowedOriginsList());
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
