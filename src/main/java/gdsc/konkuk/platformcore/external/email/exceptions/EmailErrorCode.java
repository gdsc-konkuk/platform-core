package gdsc.konkuk.platformcore.external.email.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public enum EmailErrorCode implements CustomErrorCode {

  MAIL_PARSING_ERROR("메일 전송을 다시 시도해 주세요", "메일 파싱에 실패했습니다."),
  MAIL_SENDING_ERROR("메일 전송을 다시 시도해 주세요", "메일 전송에 실패했습니다."),
  ;

  private final String message;
  private final String logMessage;

  EmailErrorCode(String message, String logMessage) {
    this.message = message;
    this.logMessage = logMessage;
  }

  @Override
  public String getLogMessage() {
    return logMessage;
  }

  @Override
  public String getName() {
    return name();
  }

  @Override
  public String getMessage() {
    return message;
  }
}
