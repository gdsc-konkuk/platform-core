package gdsc.konkuk.platformcore.application.retrospect.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RetrospectErrorCode implements CustomErrorCode {
  RETROSPECT_NOT_FOUND("회고가 존재하지 않습니다.", "[ERROR] : 회고 정보를 찾을 수 없음");

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
