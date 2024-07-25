package gdsc.konkuk.platformcore.external.email.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class EmailSendingException extends BusinessException {

  protected EmailSendingException(CustomErrorCode errorCode,
    String logMessage) {
    super(errorCode, logMessage);
  }

  public static EmailSendingException of(CustomErrorCode errorCode,
    String logMessage) {
    return new EmailSendingException(errorCode, logMessage);
  }
}
