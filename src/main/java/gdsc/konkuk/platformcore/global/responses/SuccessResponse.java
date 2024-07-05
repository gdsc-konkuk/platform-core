package gdsc.konkuk.platformcore.global.responses;

import lombok.Getter;

@Getter
public class SuccessResponse extends Response {

	private final Object data;

	private SuccessResponse(boolean isSuccess, String message, Object data) {
		super(isSuccess, message);
		this.data = data;
	}

	public static SuccessResponse of(Object data) {
		//TODO: 성공에 대한 메세지를 하나로 통일할건지 정할 필요 있음.
		return new SuccessResponse(true, "SUCCESS", data);
	}
}
