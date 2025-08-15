package gdsc.konkuk.platformcore.global.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cors")
public record CorsProps(
        List<String> allowedOrigins
) {}