package gdsc.konkuk.platformcore.application.retrospect.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class RetrospectNotFoundException extends BusinessException {
  protected RetrospectNotFoundException(CustomErrorCode errorCode, String logMessage) {
    super(errorCode, logMessage);
  }

  public static RetrospectNotFoundException of(CustomErrorCode errorCode) {
    return new RetrospectNotFoundException(errorCode, errorCode.getMessage());
  }
}
