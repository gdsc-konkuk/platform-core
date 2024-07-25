package gdsc.konkuk.platformcore.application.event.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class EventNotFoundException extends BusinessException {
  protected EventNotFoundException(CustomErrorCode errorCode, String logMessage) {
    super(errorCode, logMessage);
  }

  public static EventNotFoundException of(CustomErrorCode errorCode) {
    return new EventNotFoundException(errorCode, errorCode.getMessage());
  }
}
