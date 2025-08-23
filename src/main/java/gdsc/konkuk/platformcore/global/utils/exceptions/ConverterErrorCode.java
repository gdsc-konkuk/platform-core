package gdsc.konkuk.platformcore.global.utils.exceptions;

import gdsc.konkuk.platformcore.global.exceptions.CustomErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ConverterErrorCode implements CustomErrorCode {
    CIPHERTEXT_INVALID_FORMAT("암호화된 문자열의 형식이 올바르지 않습니다.", "[ERROR] : 암호화된 문자열의 형식이 올바르지 않음"),
    CIPHERTEXT_DECRYPTION_FAILED("암호화된 문자열 복호화에 실패했습니다.", "[ERROR] : 암호화된 문자열 복호화에 실패함"),
    CIPHERTEXT_ENCRYPTION_FAILED("문자열 암호화에 실패했습니다.", "[ERROR] : 문자열 암호화에 실패함"),
    AES_SIV_INVALID_KEY_LENGTH("키 바이트 길이가 올바르지 않습니다.", "[ERROR] : 키 바이트 길이가 올바르지 않음");


    private final String message;
    private final String logMessage;

    @Override
    public String getLogMessage() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getMessage() {
        return "";
    }
}
