package gdsc.konkuk.platformcore.global.consts;

public class PlatformConstants {

    public static final Integer SOFT_DELETE_RETENTION_MONTHS = 3;
    public static final String EMAIL_RECEIVER_NAME_REGEXP = "\\{이름}";

    public static final String API_PREFIX = "/api/v1";

    public static String apiPath(String path) {
        return API_PREFIX + path;
    }
}
