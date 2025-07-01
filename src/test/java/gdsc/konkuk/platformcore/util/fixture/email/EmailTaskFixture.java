package gdsc.konkuk.platformcore.util.fixture.email;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.application.email.dtos.EmailTaskInfo;
import gdsc.konkuk.platformcore.domain.email.entity.EmailDetail;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailTaskFixture {

    private final EmailTask fixture;

    @Builder
    public EmailTaskFixture(Long id, EmailDetail emailDetail,
        LocalDateTime sendAt) {
        this.fixture =
            EmailTask.builder()
                .id(getDefault(id, 0L))
                .emailDetail(getDefault(emailDetail,
                    EmailDetailFixture.builder().build().getFixture()))
                .sendAt(getDefault(sendAt, LocalDateTime.now().plusHours(1)))
                .build();
    }
}
