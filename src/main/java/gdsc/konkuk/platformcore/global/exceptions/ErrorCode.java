package gdsc.konkuk.platformcore.global.exceptions;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCode {

	INTERNAL_SERVER_ERROR("서버 오류입니다. 잠시후 재시도 해주세요", "[ERROR] : 예상치못한 에러 발생", 500);

	private final String message;
	private final String logMessage;
	private final int statusCode;

	ErrorCode(String message, String logMessage, int statusCode) {
		this.message = message;
		this.logMessage = logMessage;
		this.statusCode = statusCode;
	}
}
