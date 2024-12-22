package gdsc.konkuk.platformcore.application.email.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class EmailNotFoundException extends BusinessException {

    protected EmailNotFoundException(CustomErrorCode errorCode, String logMessage) {
        super(errorCode, logMessage);
    }

    public static EmailNotFoundException of(CustomErrorCode errorCode) {
        return new EmailNotFoundException(errorCode, errorCode.getMessage());
    }
}
