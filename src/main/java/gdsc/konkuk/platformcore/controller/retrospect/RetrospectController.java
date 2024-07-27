package gdsc.konkuk.platformcore.controller.retrospect;

import gdsc.konkuk.platformcore.application.retrospect.RetrospectService;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/retrospects")
@RequiredArgsConstructor
public class RetrospectController {
  private final RetrospectService retrospectService;

  @PatchMapping("/{retrospectId}")
  public ResponseEntity<SuccessResponse> updateRetrospect(
      @PathVariable Long retrospectId, @RequestBody RetrospectUpdateRequest updateRequest) {
    retrospectService.updateRetrospect(retrospectId, updateRequest.getContent());
    return ResponseEntity.ok(SuccessResponse.messageOnly());
  }
}
