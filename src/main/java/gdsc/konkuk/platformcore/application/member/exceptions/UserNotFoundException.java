package gdsc.konkuk.platformcore.application.member.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class UserNotFoundException extends BusinessException {

    protected UserNotFoundException(CustomErrorCode errorCode,
            String logMessage) {
        super(errorCode, logMessage);
    }

    public static UserNotFoundException of(CustomErrorCode errorCode) {
        return new UserNotFoundException(errorCode, errorCode.getMessage());
    }
}
