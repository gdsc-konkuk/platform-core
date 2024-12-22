package gdsc.konkuk.platformcore.global.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String logMessage;

    protected BusinessException(CustomErrorCode errorCode, String logMessage) {
        super(errorCode.getMessage());
        this.logMessage = logMessage;
    }

    public static BusinessException of(CustomErrorCode errorCode) {
        return new BusinessException(errorCode, errorCode.getLogMessage());
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
