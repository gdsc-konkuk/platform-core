package gdsc.konkuk.platformcore.application.email.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record EmailTaskDetailResponse (
        String subject,
        String content,
        List<EmailReceiverInfo> receiverInfos,
        LocalDateTime sendAt
) {
}
