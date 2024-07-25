package gdsc.konkuk.platformcore.controller.email;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gdsc.konkuk.platformcore.application.email.EmailService;
import gdsc.konkuk.platformcore.controller.email.dto.EmailSendRequest;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
public class EmailController {

  private final EmailService emailService;

  @PostMapping()
  public ResponseEntity<SuccessResponse> sendEmail(@RequestBody @Valid EmailSendRequest request) {
    Long emailId = emailService.process(request);
    return ResponseEntity.created(URI.create("/api/v1/emails/" + emailId)).body(SuccessResponse.messageOnly());
  }
}
