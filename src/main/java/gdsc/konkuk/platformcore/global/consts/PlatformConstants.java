package gdsc.konkuk.platformcore.global.consts;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlatformConstants {

    public static final List<String> allowedOrigins = List.of(
            "http://localhost:5173", "https://stage.gdsc-konkuk.dev",
            "https://gdsc-konkuk.dev", "https://admin.gdsc-konkuk.dev",
            "https://member.gdsc-konkuk.dev", "https://landing.gdsc-konkuk.dev");

    public static final Integer SOFT_DELETE_RETENTION_MONTHS = 3;

    public static final String LOGIN_NAME = "id";
    public static final String API_PREFIX = "/api/v1";

    public static final String EMAIL_RECEIVER_NAME_REGEXP = "\\{이름}";

    public static final String DISCORD_ERROR_TITLE = "\uD83E\uDDE8 치명적인 서버 에러 발생!";
    public static final String DISCORD_ERROR_DESCRIPTION =
            "\uD83C\uDD98 서버에서 치명적인 에러가 발생했습니다. 빠르게 확인해주세요. \uD83C\uDD98";
    public static final String DISCORD_ERROR_TIME_TEXT = "\uD83D\uDD50 에러 발생 시간 : ";

    public static String apiPath(String path) {
        return API_PREFIX + path;
    }
}
