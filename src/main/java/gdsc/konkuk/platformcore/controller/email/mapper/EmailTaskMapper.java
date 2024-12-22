package gdsc.konkuk.platformcore.controller.email.mapper;

import gdsc.konkuk.platformcore.controller.email.dtos.EmailTaskDetailResponse;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailTaskListResponse;
import gdsc.konkuk.platformcore.controller.email.dtos.SimpleEmailTaskResponse;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailTaskMapper {

    public static EmailTaskListResponse mapToEmailTaskPageResponse(Page<EmailTask> emailTasks) {
        return mapToEmailTaskListResponse(emailTasks.getContent());
    }

    public static EmailTaskListResponse mapToEmailTaskListResponse(List<EmailTask> emailTasks) {
        List<SimpleEmailTaskResponse> simpleEmailTaskResponses =
                emailTasks.stream()
                        .map(SimpleEmailTaskResponse::from)
                        .toList();
        return new EmailTaskListResponse(simpleEmailTaskResponses);
    }

    public static EmailTaskDetailResponse mapToEmailTaskDetailsResponse(EmailTask emailTask) {
        return new EmailTaskDetailResponse(
                emailTask.getEmailDetail(), emailTask.getEmailReceivers(), emailTask.getSendAt());
    }
}
