package gdsc.konkuk.platformcore.util.fixture.email;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetail;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailTaskFixture {

    private final EmailTask fixture;

    @Builder
    public EmailTaskFixture(Long id, EmailDetail emailDetail, EmailReceivers receivers,
            LocalDateTime sendAt) {
        this.fixture =
                EmailTask.builder()
                        .id(getDefault(id, 0L))
                        .emailDetail(getDefault(emailDetail,
                                EmailDetailFixture.builder().build().getFixture()))
                        .receivers(getDefault(
                                receivers,
                                new EmailReceivers(Set.of(
                                        EmailReceiverFixture.builder().email("ex1@gmail.com")
                                                .name("guest1").build().getFixture(),
                                        EmailReceiverFixture.builder().email("ex2@gmail.com")
                                                .name("guest2").build().getFixture()))))
                        .sendAt(getDefault(sendAt, LocalDateTime.now().plusHours(1)))
                        .build();
    }
}
