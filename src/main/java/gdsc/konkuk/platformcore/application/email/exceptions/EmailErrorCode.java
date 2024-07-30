package gdsc.konkuk.platformcore.application.email.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public enum EmailErrorCode implements CustomErrorCode {
  EMAIL_ALREADY_PROCESSED("이미 처리된 이메일입니다..", "[Error] : 이미 처리된 이메일 수정 시도."),
  EMAIL_NOT_FOUND("이메일 전송 작업을 찾을 수 없습니다.", "[Error] : 존재하지 않는 이메일 작업 접근."),
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
    return this.name();
  }

  @Override
  public String getMessage() {
    return message;
  }
}
