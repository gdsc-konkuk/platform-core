package gdsc.konkuk.platformcore.controller.member;

import gdsc.konkuk.platformcore.application.member.dtos.MemberAttendances;
import gdsc.konkuk.platformcore.controller.member.dtos.AttendanceUpdateRequest;
import gdsc.konkuk.platformcore.controller.member.dtos.MemberRegisterRequest;
import gdsc.konkuk.platformcore.controller.member.dtos.PasswordChangeRequest;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gdsc.konkuk.platformcore.application.member.MemberService;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.global.utils.SecurityUtils;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @PostMapping("check-login")
  public ResponseEntity<SuccessResponse> checkLogin() {
    return ResponseEntity.ok(SuccessResponse.messageOnly());
  }

  @PostMapping()
  public ResponseEntity<SuccessResponse> signup(
      @RequestBody @Valid MemberRegisterRequest registerRequest) {
    Member registeredMember = memberService.register(registerRequest);
    return ResponseEntity.created(getCreatedURI(registeredMember.getId()))
        .body(SuccessResponse.messageOnly());
  }

  @PostMapping("/{member-id}/password")
  public ResponseEntity<SuccessResponse> changePassword(
      @PathVariable("member-id") String memberId,
      @RequestBody @Valid PasswordChangeRequest passwordChangeRequest) {
    memberService.changePassword(memberId, passwordChangeRequest.getPassword());
    return ResponseEntity.ok(SuccessResponse.messageOnly());
  }

  @DeleteMapping()
  public ResponseEntity<SuccessResponse> withdraw() {
    Long currentId = SecurityUtils.getCurrentUserId();
    memberService.withdraw(currentId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{batch}/attendances")
  public ResponseEntity<SuccessResponse> getAttendances(
      @PathVariable String batch, @RequestParam Integer year, @RequestParam Integer month) {
    List<MemberAttendances> memberAttendanceInfoList =
        memberService.getMemberAttendanceWithBatchAndPeriod(batch, LocalDate.of(year, month, 1));
    return ResponseEntity.ok(SuccessResponse.of(memberAttendanceInfoList));
  }

  @PatchMapping("/{batch}/attendances")
  public ResponseEntity<SuccessResponse> updateAttendances(
      @PathVariable String batch,
      @RequestParam Integer year,
      @RequestParam Integer month,
      @RequestBody @Valid AttendanceUpdateRequest attendanceUpdateRequest) {
    memberService.updateAttendances(
        batch, LocalDate.of(year, month, 1), attendanceUpdateRequest.getAttendanceUpdateInfoList());
    return ResponseEntity.ok(SuccessResponse.messageOnly());
  }

  private URI getCreatedURI(Long memberId) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(memberId)
        .toUri();
  }
}
