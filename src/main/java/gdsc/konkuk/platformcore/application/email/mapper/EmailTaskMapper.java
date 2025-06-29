package gdsc.konkuk.platformcore.application.email.mapper;

import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskDetailResponse;
import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskListResponse;
import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskInfo;
import gdsc.konkuk.platformcore.application.email.dtos.SimpleEmailTaskResponse;
import gdsc.konkuk.platformcore.application.email.dtos.EmailReceiverInfo;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailTaskMapper {

    public static EmailTaskListResponse mapToEmailTaskListResponse(List<EmailTaskInfo> emailTaskDtoList) {
        return new EmailTaskListResponse(
            mapToSimpleEmailTaskResponseList(emailTaskDtoList)
        );
    }

    private static List<SimpleEmailTaskResponse> mapToSimpleEmailTaskResponseList(List<EmailTaskInfo> emailTaskDtoList) {
        return emailTaskDtoList.stream()
            .map(SimpleEmailTaskResponse::from)
            .toList();
    }

    public static EmailTaskDetailResponse mapToEmailTaskDetailResponse(
        EmailTaskInfo emailTaskInfo) {
        return new EmailTaskDetailResponse(
            emailTaskInfo.emailTask().getEmailDetail().getSubject(),
            emailTaskInfo.emailTask().getEmailDetail().getContent(),
            EmailReceiverInfo.fromValueObjectList(emailTaskInfo.emailReceivers()),
            emailTaskInfo.emailTask().getSendAt()
        );
    }

    public static List<EmailReceiver> mapToEmailReceiverList(EmailTask task, Set<EmailReceiverInfo> emailReceiverInfo) {
        return emailReceiverInfo.stream()
            .map(receiver -> EmailReceiver.builder()
                .emailTaskId(task.getId())
                .email(receiver.getEmail())
                .name(receiver.getName())
                .build())
            .toList();
    }
}
