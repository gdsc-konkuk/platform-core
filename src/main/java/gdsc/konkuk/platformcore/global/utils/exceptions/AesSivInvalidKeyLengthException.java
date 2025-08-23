package gdsc.konkuk.platformcore.global.utils.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class AesSivInvalidKeyLengthException extends BusinessException {
    protected AesSivInvalidKeyLengthException(
            CustomErrorCode errorCode, String logMessage) {
        super(errorCode, logMessage);
    }
    public static AesSivInvalidKeyLengthException of(CustomErrorCode errorCode) {
        return new AesSivInvalidKeyLengthException(errorCode, errorCode.getMessage());
    }
}
