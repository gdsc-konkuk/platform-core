package gdsc.konkuk.platformcore.application.event.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventErrorCode implements CustomErrorCode {
  EVENT_NOT_FOUND("이벤트가 존재하지 않습니다. 다시 입력해주세요", "[ERROR] : 이벤트 정보를 찾을 수 없음");

  private final String message;
  private final String logMessage;

  @Override
  public String getLogMessage() {
    return this.logMessage;
  }

  @Override
  public String getName() {
    return this.name();
  }

  @Override
  public String getMessage() {
    return this.message;
  }
}
