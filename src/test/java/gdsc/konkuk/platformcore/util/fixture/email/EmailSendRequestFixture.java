package gdsc.konkuk.platformcore.util.fixture.email;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.controller.email.dtos.EmailReceiverInfo;
import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailSendRequestFixture {

    private final EmailSendRequest fixture;

    @Builder
    public EmailSendRequestFixture(String subject, String content, LocalDateTime sendAt,
            Set<EmailReceiverInfo> receiverInfos) {
        this.fixture = EmailSendRequest.builder()
                .subject(getDefault(subject, "subject"))
                .content(getDefault(content, "content"))
                .sendAt(getDefault(sendAt, LocalDateTime.now().plusHours(1)))
                .receiverInfos(getDefault(receiverInfos,
                        EmailReceiverInfosFixture.builder().build().getFixture()))
                .build();
    }
}
