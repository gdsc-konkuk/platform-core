package gdsc.konkuk.platformcore.global.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetDefault {

    public static <T> T getDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
