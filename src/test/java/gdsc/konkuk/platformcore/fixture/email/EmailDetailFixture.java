package gdsc.konkuk.platformcore.fixture.email;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetail;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailDetailFixture {

    private final EmailDetail fixture;

    @Builder
    public EmailDetailFixture(String subject, String content) {
        this.fixture = EmailDetail.builder()
                .subject(getDefault(subject, "subject"))
                .content(getDefault(content, """
                        안녕하세요 {이름}!
                        잘 지내시나요?
                        다시 볼 날을 기대할게요 {이름}!
                        """))
                .build();
    }
}
