package gdsc.konkuk.platformcore.global.utils.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class CiphertextDecryptionFailedException extends BusinessException {
    protected CiphertextDecryptionFailedException(
            CustomErrorCode errorCode, String logMessage) {
        super(errorCode, logMessage);
    }
    public static CiphertextDecryptionFailedException of(CustomErrorCode errorCode) {
        return new CiphertextDecryptionFailedException(errorCode, errorCode.getMessage());
    }
}
