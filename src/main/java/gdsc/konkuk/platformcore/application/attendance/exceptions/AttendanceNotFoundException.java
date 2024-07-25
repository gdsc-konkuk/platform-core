package gdsc.konkuk.platformcore.application.attendance.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class AttendanceNotFoundException extends BusinessException {
  protected AttendanceNotFoundException(CustomErrorCode errorCode, String logMessage) {
    super(errorCode, logMessage);
  }

  public static AttendanceNotFoundException of(CustomErrorCode errorCode) {
    return new AttendanceNotFoundException(errorCode, errorCode.getMessage());
  }
}
