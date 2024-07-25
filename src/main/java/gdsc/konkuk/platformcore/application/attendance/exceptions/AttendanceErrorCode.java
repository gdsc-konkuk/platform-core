package gdsc.konkuk.platformcore.application.attendance.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AttendanceErrorCode implements CustomErrorCode {
  ATTENDANCE_NOT_FOUND("출석 정보가 존재하지 않습니다", "[ERROR] : 출석 정보를 찾을 수 없음"),
  INVALID_QR_UUID("유효하지 않은 QR 코드입니다", "[ERROR] : 유효하지 않은 QR 코드"),
  PARTICIPANT_NOT_FOUND("참가자 정보가 존재하지 않습니다", "[ERROR] : 참가자 정보를 찾을 수 없음");

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
