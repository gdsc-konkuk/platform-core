package gdsc.konkuk.platformcore.application.attendance.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class QrInvalidException extends BusinessException {
  protected QrInvalidException(CustomErrorCode errorCode, String logMessage) {
    super(errorCode, logMessage);
  }

  public static QrInvalidException of(CustomErrorCode errorCode) {
    return new QrInvalidException(errorCode, errorCode.getMessage());
  }
}
