package gdsc.konkuk.platformcore.application.email.dtos;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetail;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.time.LocalDateTime;
import java.util.List;

public record EmailTaskUpsertCommand(
    EmailDetail emailTaskDetail,
    List<EmailReceiver> emailReceivers,
    LocalDateTime sendAt
) {

    public static EmailTask toEntity(EmailTaskUpsertCommand command) {
        return EmailTask.builder()
            .emailDetail(command.emailTaskDetail())
            .emailReceivers(command.emailReceivers())
            .sendAt(command.sendAt())
            .build();
    }
}
