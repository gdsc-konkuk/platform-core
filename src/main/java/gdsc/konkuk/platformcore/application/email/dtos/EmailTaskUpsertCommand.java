package gdsc.konkuk.platformcore.application.email.dtos;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetail;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.time.LocalDateTime;
import java.util.Set;

public record EmailTaskUpsertCommand(
    EmailDetail emailTaskDetail,
    Set<EmailReceiverInfo> emailReceiverInfos,
    LocalDateTime sendAt
) {

    public static EmailTask toEntity(EmailTaskUpsertCommand command) {
        return EmailTask.builder()
            .emailDetail(command.emailTaskDetail())
            .sendAt(command.sendAt())
            .build();
    }
}
