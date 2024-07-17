package gdsc.konkuk.platformcore.global.responses;

import lombok.Getter;

@Getter
public class SuccessResponse extends Response {

	private final Object data;

	private SuccessResponse(String message, Object data) {
		super(true, message);
		this.data = data;
	}
	public static SuccessResponse messageOnly() {
		return new SuccessResponse("SUCCESS", null);
	}
	public static SuccessResponse of(Object data) {
		return new SuccessResponse("SUCCESS", data);
	}
}
