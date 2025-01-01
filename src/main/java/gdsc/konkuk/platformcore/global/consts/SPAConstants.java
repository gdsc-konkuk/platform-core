package gdsc.konkuk.platformcore.global.consts;

public class SPAConstants {

    public static final String SPA_ADMIN_BASE_URL = "https://admin.gdsc-konkuk.dev";
    public static final String SPA_ADMIN_LOGIN_REDIRECT_URL =
            SPA_ADMIN_BASE_URL + "/oauth/callback";
    public static final String SPA_ADMIN_ATTENDANCE_SUCCESS_REDIRECT_URL =
            SPA_ADMIN_BASE_URL + "/attendance-return/success";
    public static final String SPA_ADMIN_ATTENDANCE_FAIL_REDIRECT_URL =
            SPA_ADMIN_BASE_URL + "/attendance-return/fail";
}
