package gdsc.konkuk.platformcore.application.member.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberErrorCode implements CustomErrorCode {

    USER_NOT_FOUND("사용자가 존재하지 않습니다. 다시 입력해주세요", "[ERROR] : 사용자 정보를 찾을 수 없음"),
    INVALID_USER_INFO("사용자 정보가 올바르지 않습니다. 다시 입력해주세요", "[ERROR] : 사용자 정보가 올바르지 않음"),
    DEACTIVATED_USER("비활성화된 사용자입니다. 다시 확인해주세요", "[ERROR] : 비활성화된 사용자"),
    USER_ALREADY_EXISTS("이미 존재하는 사용자입니다.", "[ERROR] : 이미 존재하는 사용자"),
    USER_PASSWORD_INVALID("비밀번호가 일치하지 않습니다. 다시 입력해주세요", "[ERROR] : 비밀번호가 일치하지 않음"),
    USER_NOT_ALLOWED("권한이 없습니다. 다시 확인해주세요", "[ERROR] : 권한이 없음"),
    USER_ALREADY_DELETED("이미 삭제된 사용자입니다.", "[ERROR] : 이미 삭제된 사용자");

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
