package gdsc.konkuk.platformcore.application.email.dtos;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record SimpleEmailTaskResponse (
        Long id,
        String subject,
        Set<EmailReceiverInfo> receiverInfos,
        LocalDateTime sendAt,
        Boolean isSent
){

    public static SimpleEmailTaskResponse from(EmailTaskInfo emailTaskInfo) {
        var emailTask = emailTaskInfo.emailTask();
        var emailDetail = emailTaskInfo.emailTask().getEmailDetail();
        Set<EmailReceiverInfo> receiverInfos =
                EmailReceiverInfo.fromValueObject(emailTaskInfo.emailReceivers());
        return SimpleEmailTaskResponse.builder()
                .id(emailTask.getId())
                .subject(emailDetail.getSubject())
                .receiverInfos(receiverInfos)
                .sendAt(emailTask.getSendAt())
                .isSent(emailTask.isSent())
                .build();
    }
}
