package gdsc.konkuk.platformcore.controller.email.dtos;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetail;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
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

    public static EmailTask toEntity(EmailSendRequest request) {
        EmailDetail details = new EmailDetail(request.getSubject(), request.getContent());
        EmailReceivers receivers = new EmailReceivers(request.toEmailReceivers());
        return EmailTask.builder()
                .emailDetail(details)
                .receivers(receivers)
                .sendAt(request.getSendAt())
                .build();
    }

    public EmailDetail toEmailDetails() {
        return new EmailDetail(subject, content);
    }

    public Set<EmailReceiver> toEmailReceivers() {
        return receiverInfos
                .stream()
                .map(EmailReceiverInfo::toValueObject)
                .collect(Collectors.toSet());
    }
}
