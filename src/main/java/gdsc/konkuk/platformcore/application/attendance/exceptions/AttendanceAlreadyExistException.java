package gdsc.konkuk.platformcore.application.attendance.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class AttendanceAlreadyExistException extends BusinessException {
  protected AttendanceAlreadyExistException(CustomErrorCode errorCode, String logMessage) {
    super(errorCode, logMessage);
  }

  public static AttendanceAlreadyExistException of(CustomErrorCode errorCode) {
    return new AttendanceAlreadyExistException(errorCode, errorCode.getMessage());
  }
}
