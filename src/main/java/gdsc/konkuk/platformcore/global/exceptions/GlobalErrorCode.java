package gdsc.konkuk.platformcore.global.exceptions;

import lombok.Getter;

@Getter
public enum GlobalErrorCode implements CustomErrorCode {

  // DTO Validation에서 발생한 에러 처리를 위한 코드
  ARGUMENT_NOT_VALID("잘못된 입력입니다. 다시 확인해주세요", "[Error] : 컨트롤러 벨리데이션 실패 잘못된 인자"),
  ARGUMENT_MISSING("필수 입력값이 누락되었습니다. 다시 확인해주세요", "[Error] : 컨트롤러 벨리데이션 실패 필수 인자 누락"),

  METHOD_NOT_ALLOWED("허용되지 않은 메소드입니다.", "[Error] : 허용되지 않은 메소드"),
  NOT_FOUND("찾을 수 없습니다. 다시 확인해주세요", "[Error] : 404 에러 발생"),

  INTERNAL_SERVER_ERROR("서버 오류입니다. 잠시후 재시도 해주세요", "[Error] : 예상치못한 에러 발생"),

  SCHEDULED_TASK_NOT_AVAILABLE("해당 작업은 유효하지 않습니다.", "[Error] : 인메모리 저장소에 예약된 작업을 찾을 수 없음");

  private final String message;
  private final String logMessage;

  GlobalErrorCode(String message, String logMessage) {
    this.message = message;
    this.logMessage = logMessage;
  }

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
