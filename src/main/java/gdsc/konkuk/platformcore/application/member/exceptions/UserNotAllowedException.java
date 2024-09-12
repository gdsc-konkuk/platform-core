package gdsc.konkuk.platformcore.application.member.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class UserNotAllowedException extends BusinessException {
  protected UserNotAllowedException(CustomErrorCode errorCode, String logMessage) {
    super(errorCode, logMessage);
  }

  public static UserNotAllowedException of(CustomErrorCode errorCode) {
    return new UserNotAllowedException(errorCode, errorCode.getMessage());
  }
}
