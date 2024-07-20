package gdsc.konkuk.platformcore.application.member.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;
import lombok.Getter;

@Getter
public enum MemberErrorCode implements CustomErrorCode {

	USER_NOT_FOUND("사용자가 존재하지 않습니다. 다시 입력해주세요", "[ERROR] : 사용자 정보를 찾을 수 없음"),
	INVALID_USER_INFO("사용자 정보가 올바르지 않습니다. 다시 입력해주세요", "[ERROR] : 사용자 정보가 올바르지 않음"),
	DEACTIVATED_USER("비활성화된 사용자입니다. 다시 확인해주세요", "[ERROR] : 탈퇴한 사용자"),
	USER_ALREADY_EXISTS("이미 존재하는 사용자입니다.", "[ERROR] : 이미 존재하는 사용자");

	private final String message;
	private final String logMessage;

	MemberErrorCode(String message, String logMessage) {
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
