package gdsc.konkuk.platformcore.controller.email;

import gdsc.konkuk.platformcore.application.email.EmailService;
import gdsc.konkuk.platformcore.application.email.EmailTaskFacade;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailTaskDetailResponse;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailTaskListResponse;
import gdsc.konkuk.platformcore.controller.email.mapper.EmailTaskMapper;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.global.responses.SuccessResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final EmailTaskFacade emailTaskFacade;

    @GetMapping
    public ResponseEntity<SuccessResponse> getAllEmailTask() {
        EmailTaskListResponse emailTasks = EmailTaskMapper.mapToEmailTaskListResponse(
                emailService.getAllTaskAsList());
        return ResponseEntity.ok(SuccessResponse.of(emailTasks));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<SuccessResponse> getEmailTask(@PathVariable Long taskId) {
        EmailTaskDetailResponse emailTask = EmailTaskMapper.mapToEmailTaskDetailsResponse(
                emailService.getTaskDetails(taskId));
        return ResponseEntity.ok(SuccessResponse.of(emailTask));
    }

    @PostMapping()
    public ResponseEntity<SuccessResponse> scheduleEmailTask(
            @RequestBody @Valid EmailSendRequest request) {
        EmailTask emailTask = emailTaskFacade.register(request);
        return ResponseEntity.created(URI.create("/api/v1/emails/" + emailTask.getId()))
                .body(SuccessResponse.messageOnly());
    }

    @PatchMapping("/{emailId}")
    public ResponseEntity<SuccessResponse> updateEmailTask(@PathVariable Long emailId,
            @RequestBody @Valid EmailSendRequest request) {
        emailTaskFacade.update(emailId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{emailId}")
    public ResponseEntity<SuccessResponse> deleteEmailTask(@PathVariable Long emailId) {
        emailTaskFacade.cancel(emailId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("")
    public ResponseEntity<SuccessResponse> deleteEmailTaskInBatch(
            @RequestParam List<Long> emailIds) {
        emailTaskFacade.cancelAll(emailIds);
        return ResponseEntity.noContent().build();
    }
}
