package gdsc.konkuk.platformcore.application.email.dtos;

import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.util.List;

public record EmailTaskInfo(
    EmailTask emailTask,
    List<EmailReceiver> emailReceivers
) {

}
