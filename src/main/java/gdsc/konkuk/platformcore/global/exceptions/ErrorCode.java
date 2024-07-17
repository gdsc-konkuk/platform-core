package gdsc.konkuk.platformcore.global.exceptions;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCode {

	INTERNAL_SERVER_ERROR("서버 오류입니다. 잠시후 재시도 해주세요", "[ERROR] : 예상치못한 에러 발생", 500),
	USER_NOT_FOUND("사용자가 존재하지 않습니다. 다시 입력해주세요", "[ERROR] : 사용자 정보를 찾을 수 없음", 404),
	INVALID_USER_INFO("사용자 정보가 올바르지 않습니다. 다시 입력해주세요", "[ERROR] : 사용자 정보가 올바르지 않음", 400),
	DEACTIVATED_USER("비활성화된 사용자입니다. 다시 확인해주세요", "[ERROR] : 탈퇴한 사용자", 400),
	USER_ALREADY_EXISTS("이미 존재하는 사용자입니다.", "[ERROR] : 이미 존재하는 사용자", 400);

	private final String message;
	private final String logMessage;
	private final int statusCode;

	ErrorCode(String message, String logMessage, int statusCode) {
		this.message = message;
		this.logMessage = logMessage;
		this.statusCode = statusCode;
	}
}
