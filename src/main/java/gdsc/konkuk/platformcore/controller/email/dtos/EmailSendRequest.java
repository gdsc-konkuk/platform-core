package gdsc.konkuk.platformcore.controller.email.dtos;

import gdsc.konkuk.platformcore.application.email.dtos.EmailReceiverInfo;
import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskUpsertCommand;
import gdsc.konkuk.platformcore.domain.email.entity.EmailDetail;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailSendRequest {

    @NotEmpty
    private String subject;

    @NotEmpty
    private String content;

    @NotNull
    private Set<EmailReceiverInfo> receiverInfos;

    @NotNull
    private LocalDateTime sendAt;

    @Builder
    public EmailSendRequest(String subject, String content, Set<EmailReceiverInfo> receiverInfos,
            LocalDateTime sendAt) {
        this.subject = subject;
        this.content = content;
        this.receiverInfos = receiverInfos;
        this.sendAt = sendAt;
    }

    public static EmailTaskUpsertCommand toCommand(EmailSendRequest request) {
        return new EmailTaskUpsertCommand(
                new EmailDetail(request.getSubject(), request.getContent()),
                request.getReceiverInfos(),
                request.getSendAt()
        );
    }

}
