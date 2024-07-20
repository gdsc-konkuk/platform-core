package gdsc.konkuk.platformcore.global.exceptions;

import lombok.Getter;

@Getter
public enum GlobalErrorCode implements CustomErrorCode{

	// DTO Validation에서 발생한 에러 처리를 위한 코드
	ARGUMENT_NOT_VALID("잘못된 입력입니다. 다시 확인해주세요", "[ERROR] : 400 컨트롤러 벨리데이션 실패 잘못된 인자"),

	NOT_FOUND("찾을 수 없습니다. 다시 확인해주세요", "[ERROR] : 404 에러 발생"),
	INTERNAL_SERVER_ERROR("서버 오류입니다. 잠시후 재시도 해주세요", "[ERROR] : 500 예상치못한 에러 발생");

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
