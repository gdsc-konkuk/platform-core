package gdsc.konkuk.platformcore.global.configs;


import java.util.Arrays;

import gdsc.konkuk.platformcore.global.props.CorsProps;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(CorsProps.class)
public class CorsConfig {

    private final CorsProps corsProps;

    @Bean
    public UrlBasedCorsConfigurationSource getConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(corsProps.allowedOrigins());
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
