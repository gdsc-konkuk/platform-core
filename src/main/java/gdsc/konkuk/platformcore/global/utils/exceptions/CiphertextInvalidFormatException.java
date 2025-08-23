package gdsc.konkuk.platformcore.global.utils.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class CiphertextInvalidFormatException extends BusinessException {
    protected CiphertextInvalidFormatException(
            CustomErrorCode errorCode, String logMessage) {
        super(errorCode, logMessage);
    }
    public static CiphertextInvalidFormatException of(CustomErrorCode errorCode) {
        return new CiphertextInvalidFormatException(errorCode, errorCode.getMessage());
    }
}
