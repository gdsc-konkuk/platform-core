package gdsc.konkuk.platformcore.controller.email.dtos;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetail;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailTaskDetailResponse {

    private final String subject;
    private final String content;
    private final List<EmailReceiverInfo> receiverInfos;
    private final LocalDateTime sendAt;

    @Builder
    public EmailTaskDetailResponse(EmailDetail emailDetail, EmailReceivers emailReceivers,
            LocalDateTime sendAt) {
        this.subject = emailDetail.getSubject();
        this.content = emailDetail.getContent();
        this.receiverInfos = emailReceivers
                .getReceivers()
                .stream()
                .map(EmailReceiverInfo::fromValueObject)
                .toList();
        this.sendAt = sendAt;
    }
}
