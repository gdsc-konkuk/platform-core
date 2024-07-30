package gdsc.konkuk.platformcore.application.email.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class EmailAlreadyProcessedException extends BusinessException {

  protected EmailAlreadyProcessedException(CustomErrorCode errorCode, String logMessage) {
    super(errorCode, logMessage);
  }

  public static EmailAlreadyProcessedException of(CustomErrorCode customErrorCode) {
    return new EmailAlreadyProcessedException(customErrorCode, customErrorCode.getMessage());
  }
}
