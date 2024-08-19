package gdsc.konkuk.platformcore.controller.email.mapper;

import java.util.List;
import java.util.stream.Collectors;

import gdsc.konkuk.platformcore.controller.email.dtos.EmailTaskDetailsResponse;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailTaskListResponse;
import gdsc.konkuk.platformcore.controller.email.dtos.SimpleEmailTaskResponse;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailTaskMapper {

  public static EmailTaskListResponse mapToEmailTaskListResponse(List<EmailTask> emailTasks) {
    List<SimpleEmailTaskResponse> simpleEmailTaskResponses =
        emailTasks.stream()
            .flatMap(
                emailTask ->
                    emailTask.getEmailReceivers().getReceivers().stream()
                        .map(receiver -> SimpleEmailTaskResponse.of(emailTask, receiver)))
            .collect(Collectors.toList());
    return new EmailTaskListResponse(simpleEmailTaskResponses);
  }

  public static EmailTaskDetailsResponse mapToEmailTaskDetailsResponse(EmailTask emailTask) {
    return new EmailTaskDetailsResponse(
        emailTask.getEmailDetails(), emailTask.getEmailReceivers(), emailTask.getSendAt());
  }
}
