package gdsc.konkuk.platformcore.application.member.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.ErrorCode;

public class UserAlreadyExistException extends BusinessException {

	protected UserAlreadyExistException(ErrorCode errorCode, String logMessage) {
		super(errorCode, logMessage);
	}

	public static UserAlreadyExistException of(ErrorCode errorCode) {
		return new UserAlreadyExistException(errorCode, errorCode.getLogMessage());
	}
}
