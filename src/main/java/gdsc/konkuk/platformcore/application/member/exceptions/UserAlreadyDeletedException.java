package gdsc.konkuk.platformcore.application.member.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class UserAlreadyDeletedException extends BusinessException {

  protected UserAlreadyDeletedException(CustomErrorCode errorCode,
    String logMessage) {
    super(errorCode, logMessage);
  }

  public static UserAlreadyDeletedException of(CustomErrorCode errorCode) {
    return new UserAlreadyDeletedException(errorCode, errorCode.getMessage());
  }
}
