package gdsc.konkuk.platformcore.controller.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gdsc.konkuk.platformcore.application.member.MemberRegisterRequest;
import gdsc.konkuk.platformcore.application.member.MemberService;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PostMapping("")
	public ResponseEntity<SuccessResponse> signup(@RequestBody MemberRegisterRequest registerRequest) {
		memberService.register(registerRequest);
		return ResponseEntity.status(201).body(SuccessResponse.messageOnly());
	}

}
