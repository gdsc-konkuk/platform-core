package gdsc.konkuk.platformcore.controller.member;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gdsc.konkuk.platformcore.application.member.MemberService;
import gdsc.konkuk.platformcore.global.controller.SecurityUtils;
import gdsc.konkuk.platformcore.global.responses.Response;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PostMapping()
	public ResponseEntity<Response> signup(@RequestBody @Valid MemberRegisterRequest registerRequest) {
		memberService.register(registerRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.messageOnly());
	}

	@DeleteMapping()
	public ResponseEntity<Response> withdraw() {
		Long currentId = SecurityUtils.getCurrentUserId();
		memberService.withdraw(currentId);
		return ResponseEntity.ok(SuccessResponse.messageOnly());
	}

}
