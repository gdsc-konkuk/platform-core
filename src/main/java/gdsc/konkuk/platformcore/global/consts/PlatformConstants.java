package gdsc.konkuk.platformcore.global.consts;

import java.util.List;

public class PlatformConstants {

    public static final List<String> allowedOrigins = List.of(
            "http://localhost:5173", "https://stage.gdsc-konkuk.dev",
            "https://gdsc-konkuk.dev", "https://admin.gdsc-konkuk.dev",
            "https://member.gdsc-konkuk.dev", "https://landing.gdsc-konkuk.dev");

    public static final Integer SOFT_DELETE_RETENTION_MONTHS = 3;
    public static final String EMAIL_RECEIVER_NAME_REGEXP = "\\{이름}";

    public static final String API_PREFIX = "/api/v1";

    public static String apiPath(String path) {
        return API_PREFIX + path;
    }
}
