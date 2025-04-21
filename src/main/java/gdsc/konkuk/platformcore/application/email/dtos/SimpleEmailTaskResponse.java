package gdsc.konkuk.platformcore.application.email.dtos;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetail;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
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

    public static SimpleEmailTaskResponse from(EmailTask emailTask) {
        EmailDetail emailDetail = emailTask.getEmailDetail();
        Set<EmailReceiverInfo> receiverInfos =
                EmailReceiverInfo.fromValueObject(emailTask.getEmailReceivers());
        return SimpleEmailTaskResponse.builder()
                .id(emailTask.getId())
                .subject(emailDetail.getSubject())
                .receiverInfos(receiverInfos)
                .sendAt(emailTask.getSendAt())
                .isSent(emailTask.isSent())
                .build();
    }
}
