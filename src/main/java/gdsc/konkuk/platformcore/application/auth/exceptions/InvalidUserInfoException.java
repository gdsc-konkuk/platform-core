package gdsc.konkuk.platformcore.application.auth.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.ErrorCode;

public class InvalidUserInfoException extends BusinessException {

	private InvalidUserInfoException(String message, String logMessage) {
		super(message, logMessage);
	}

	public static InvalidUserInfoException of(ErrorCode errorCode) {
		return new InvalidUserInfoException(errorCode.getMessage(), errorCode.getLogMessage());
	}

}

