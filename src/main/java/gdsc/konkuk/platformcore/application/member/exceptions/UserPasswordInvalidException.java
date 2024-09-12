package gdsc.konkuk.platformcore.application.member.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class UserPasswordInvalidException extends BusinessException {
  protected UserPasswordInvalidException(CustomErrorCode errorCode, String logMessage) {
    super(errorCode, logMessage);
  }

  public static UserPasswordInvalidException of(CustomErrorCode errorCode) {
    return new UserPasswordInvalidException(errorCode, errorCode.getMessage());
  }
}
