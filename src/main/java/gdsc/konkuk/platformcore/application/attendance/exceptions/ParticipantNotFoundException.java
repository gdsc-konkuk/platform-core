package gdsc.konkuk.platformcore.application.attendance.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.BusinessException;
import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;

public class ParticipantNotFoundException extends BusinessException {

    protected ParticipantNotFoundException(CustomErrorCode errorCode, String logMessage) {
        super(errorCode, logMessage);
    }

    public static ParticipantNotFoundException of(CustomErrorCode errorCode) {
        return new ParticipantNotFoundException(errorCode, errorCode.getMessage());
    }
}
