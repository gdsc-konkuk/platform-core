package gdsc.konkuk.platformcore.global.consts;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@ConfigurationProperties("domain")
public class SPAConstants {

    private final String admin;

    public String getSpaAdminLoginRedirectUrl() {
        return admin + "/oauth/callback";
    }
    public String getSpaAttendanceSuccessRedirectUrl() {
        return admin + "/attendance-return/success";
    }
    public String getSpaAttendanceFailRedirectUrl() {
        return admin + "/attendance-return/fail";
    }
}
