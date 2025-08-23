package gdsc.konkuk.platformcore.external.security;


import gdsc.konkuk.platformcore.global.utils.AesSivConverter;
import gdsc.konkuk.platformcore.global.utils.exceptions.CiphertextDecryptionFailedException;
import gdsc.konkuk.platformcore.global.utils.exceptions.CiphertextInvalidFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class AesSivConverterTest {

    private static final String TEST_KEY_B64 =
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";

    private AesSivConverter newConverter(String keyB64) {
        try {
            return new AesSivConverter(keyB64);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Round-trip: 암호화 후 복호화하면 원문과 일치한다")
    void roundTrip_encryptThenDecrypt() {
        var converter = newConverter(TEST_KEY_B64);
        String plaintext = "안녕하세요! AES-SIV-256 테스트입니다. 🚀";

        String hexCipher = converter.convertToDatabaseColumn(plaintext);
        String decrypted = converter.convertToEntityAttribute(hexCipher);

        assertEquals(plaintext, decrypted);
        assertTrue(hexCipher.matches("^[0-9a-f]+$"));
    }

    @Test
    @DisplayName("Deterministic: 동일한 평문은 항상 동일한 암호문(HEX)을 생성한다")
    void deterministic_samePlaintextSameCiphertext() {
        var converter = newConverter(TEST_KEY_B64);
        String plaintext = "same-text";

        String c1 = converter.convertToDatabaseColumn(plaintext);
        String c2 = converter.convertToDatabaseColumn(plaintext);

        assertEquals(c1, c2);
    }

    @Test
    @DisplayName("Flexible Decode: HEX/B64/URL-B64 모두 복호화 가능")
    void flexibleDecode_hex_base64_urlBase64() {
        var converter = newConverter(TEST_KEY_B64);
        String plaintext = "multi-encoding-ok";

        // DB에 저장되는 기본 포맷: HEX
        String hexCipher = converter.convertToDatabaseColumn(plaintext);
        byte[] bytes = hexToBytes(hexCipher);

        // 1) HEX 입력
        String dec1 = converter.convertToEntityAttribute(hexCipher);
        assertEquals(plaintext, dec1);

        // 2) 표준 Base64 입력
        String b64 = Base64.getEncoder().encodeToString(bytes);
        String dec2 = converter.convertToEntityAttribute(b64);
        assertEquals(plaintext, dec2);

        // 3) URL-safe Base64 입력
        String b64url = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String dec3 = converter.convertToEntityAttribute(b64url);
        assertEquals(plaintext, dec3);
    }

    @Test
    @DisplayName("무결성 위반: 암호문을 1바이트라도 변조하면 복호화 실패")
    void integrityFail_whenTampered() {
        var converter = newConverter(TEST_KEY_B64);
        String plaintext = "tamper-test";

        String hexCipher = converter.convertToDatabaseColumn(plaintext);

        // 마지막 바이트 토글
        byte[] bytes = hexToBytes(hexCipher);
        bytes[bytes.length - 1] ^= 0x01; // flip last bit
        String tamperedHex = toHex(bytes);

        var ex = assertThrows(CiphertextDecryptionFailedException.class,
                () -> converter.convertToEntityAttribute(tamperedHex));
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("키 불일치: 다른 키로 복호화 시도하면 실패")
    void decryptWithDifferentKey_fails() {
        var converter1 = newConverter(TEST_KEY_B64);
        String plaintext = "wrong-key-fail";

        String hexCipher = converter1.convertToDatabaseColumn(plaintext);

        // 기존 테스트 키(Base64) → 64바이트로 디코드 후 1바이트만 뒤집어 다른 키 생성
        byte[] k = Base64.getDecoder().decode(TEST_KEY_B64);
        k[0] ^= 0x01;  // 첫 바이트 비트 토글 → 서로 다른 64바이트 키 보장
        String otherKeyB64 = Base64.getEncoder().encodeToString(k);

        var converter2 = newConverter(otherKeyB64);

        var ex = assertThrows(CiphertextDecryptionFailedException.class,
                () -> converter2.convertToEntityAttribute(hexCipher));
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("Null/빈 문자열 처리: null, \"NULL\", 공백은 null 반환")
    void nullAndEmptyHandling() {
        var converter = newConverter(TEST_KEY_B64);

        assertNull(converter.convertToDatabaseColumn(null));

        assertNull(converter.convertToEntityAttribute(null));
        assertNull(converter.convertToEntityAttribute(""));
        assertNull(converter.convertToEntityAttribute("   "));
        assertNull(converter.convertToEntityAttribute("NULL"));
        assertNull(converter.convertToEntityAttribute(" null "));
    }

    @Test
    @DisplayName("형식 오류: HEX/B64가 아닌 문자열은 예외")
    void invalidFormat_throws() {
        var converter = newConverter(TEST_KEY_B64);
        var ex = assertThrows(CiphertextInvalidFormatException.class,
                () -> converter.convertToEntityAttribute("**not-a-valid-cipher**"));
        assertNull(ex.getCause());
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
        var sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}