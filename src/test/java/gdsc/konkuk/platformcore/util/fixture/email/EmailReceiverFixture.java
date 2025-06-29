package gdsc.konkuk.platformcore.util.fixture.email;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailReceiverFixture {

    private final EmailReceiver fixture;

    @Builder
    public EmailReceiverFixture(Long taskId, String email, String name) {
        this.fixture = EmailReceiver.builder()
                .emailTaskId(getDefault(taskId, 0L))
                .email(getDefault(email, "ex@gmail.com"))
                .name(getDefault(name, "guest"))
                .build();
    }
}
