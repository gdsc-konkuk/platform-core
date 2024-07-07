package gdsc.konkuk.platformcore.global.responses;

import gdsc.konkuk.platformcore.global.exceptions.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse extends Response{

	private final String errorCode;

	private ErrorResponse(final ErrorCode errorCode) {
		super(false, errorCode.getMessage());
		this.errorCode = errorCode.toString();
	}

	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(errorCode);
	}

}
