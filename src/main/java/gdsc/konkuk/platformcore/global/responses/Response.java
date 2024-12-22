package gdsc.konkuk.platformcore.global.responses;

import lombok.Getter;

@Getter
public class Response {

    private final boolean isSuccess;
    private final String message;

    public Response(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

}
