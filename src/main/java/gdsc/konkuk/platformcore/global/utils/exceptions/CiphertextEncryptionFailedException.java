package gdsc.konkuk.platformcore.global.utils.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class CiphertextEncryptionFailedException extends BusinessException {
  protected CiphertextEncryptionFailedException(
          CustomErrorCode errorCode, String logMessage) {
    super(errorCode, logMessage);
  }
    public static CiphertextEncryptionFailedException of(CustomErrorCode errorCode) {
        return new CiphertextEncryptionFailedException(errorCode, errorCode.getMessage());
    }
}
