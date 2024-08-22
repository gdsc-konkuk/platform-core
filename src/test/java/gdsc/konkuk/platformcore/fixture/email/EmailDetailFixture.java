package gdsc.konkuk.platformcore.fixture.email;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetail;

public class EmailDetailFixture {
  public static final String EMAIL_SUBJECT = "subject";
  public static final String EMAIL_CONTENT = """
    안녕하세요 {이름}!
    잘 지내시나요?
    다시 볼 날을 기대할게요 {이름}!
    """;

  public static EmailDetail getEmailDetailFixture() {
    return EmailDetail.builder()
        .subject(EMAIL_SUBJECT)
        .content(EMAIL_CONTENT)
        .build();
  }
}
