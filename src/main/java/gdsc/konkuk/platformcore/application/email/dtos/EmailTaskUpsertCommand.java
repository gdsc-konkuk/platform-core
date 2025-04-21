package gdsc.konkuk.platformcore.application.email.dtos;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetail;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.time.LocalDateTime;

public record EmailTaskUpsertCommand(
    EmailDetail emailTaskDetail,
    EmailReceivers emailReceivers,
    LocalDateTime sendAt
) {

    public static EmailTask toEntity(EmailTaskUpsertCommand command) {
        return EmailTask.builder()
            .emailDetail(command.emailTaskDetail())
            .receivers(command.emailReceivers())
            .sendAt(command.sendAt())
            .build();
    }
}
