package gdsc.konkuk.platformcore.global.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

	private final String logMessage;

	protected BusinessException(String message, String logMessage) {
		super(message);
		this.logMessage = logMessage;
	}

	protected BusinessException(ErrorCode errorCode, String logMessage) {
		super(errorCode.getMessage());
		this.logMessage = logMessage;
	}

	public static BusinessException of(ErrorCode errorCode) {
		return new BusinessException(errorCode, errorCode.getLogMessage());
	}

}
