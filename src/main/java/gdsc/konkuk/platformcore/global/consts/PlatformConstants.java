package gdsc.konkuk.platformcore.global.consts;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "cors")
public class PlatformConstants {

    private String allowedOrigins;

    public List<String> getAllowedOriginsList() {
        return Arrays.stream(allowedOrigins.split(","))
                     .map(String::trim)
                     .toList();
    }

    public static final Integer SOFT_DELETE_RETENTION_MONTHS = 3;
    public static final String EMAIL_RECEIVER_NAME_REGEXP = "\\{이름}";

    public static final String API_PREFIX = "/api/v1";

    public static String apiPath(String path) {
        return API_PREFIX + path;
    }
}
