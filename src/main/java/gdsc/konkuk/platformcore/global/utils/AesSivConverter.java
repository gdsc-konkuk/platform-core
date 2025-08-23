package gdsc.konkuk.platformcore.global.utils;

import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.daead.DeterministicAeadConfig;
import com.google.crypto.tink.subtle.AesSiv;
import gdsc.konkuk.platformcore.global.utils.exceptions.*;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
@Component
@Converter
@Slf4j
public class AesSivConverter implements AttributeConverter<String, String> {

    private final DeterministicAead daead;

    public AesSivConverter(@Value("${spring.security.database.encryption.key}") String encryptionKey) throws GeneralSecurityException {
        DeterministicAeadConfig.register();

        byte[] keyBytes = Base64.getDecoder().decode(encryptionKey.trim());
        if (keyBytes.length != 64) {
            throw AesSivInvalidKeyLengthException.of(ConverterErrorCode.AES_SIV_INVALID_KEY_LENGTH);
        }
        this.daead = new AesSiv(keyBytes);
    }

    // 엔티티의 자바 객체 값을 DB에 저장하기 전에 변환
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            byte[] plaintext = attribute.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertextCtTag = daead.encryptDeterministically(plaintext, new byte[0]);
            return toHex(ciphertextCtTag);
        } catch (GeneralSecurityException e) {
            log.error("Encryption failed", e);
            throw CiphertextEncryptionFailedException.of(ConverterErrorCode.CIPHERTEXT_ENCRYPTION_FAILED);
        }
    }

    // DB에 저장된 값을 엔티티 객체로 읽어올 때 변환
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String s = dbData.trim();
        if (s.isEmpty() || s.equalsIgnoreCase("NULL")) return null;

        try {
            byte[] decoded = decodeFlexible(s);
            byte[] pt = daead.decryptDeterministically(decoded, new byte[0]);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            log.error("Decryption failed", e);
            throw CiphertextDecryptionFailedException.of(ConverterErrorCode.CIPHERTEXT_DECRYPTION_FAILED);
        } catch (IllegalArgumentException e) {
            log.error("Decryption failed", e);
            throw CiphertextInvalidFormatException.of(ConverterErrorCode.CIPHERTEXT_INVALID_FORMAT);
        }
    }

    private static byte[] decodeFlexible(String s) {
        String t = s.trim();

        if (t.matches("^[0-9a-fA-F]+$") && t.length() % 2 == 0) {
            return hexToBytes(t);
        }

        try {
            return Base64.getDecoder().decode(padB64(t));
        } catch (Exception ignored) {
        }

        try {
            return Base64.getUrlDecoder().decode(padB64(t));
        } catch (Exception ignored) {
        }

        throw CiphertextInvalidFormatException.of(ConverterErrorCode.CIPHERTEXT_INVALID_FORMAT);
    }

    private static String padB64(String s) {
        int m = s.length() % 4;
        return m == 0 ? s : (s + "====".substring(m));
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return out;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}