package gdsc.konkuk.platformcore.fixture.email;

import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;

public class EmailReceiverFixture {
  public static final String EMAIL_RECEIVER_1_EMAIL = "ex@gmail.com";
  public static final String EMAIL_RECEIVER_1_NAME = "guest1";

  public static final String EMAIL_RECEIVER_2_EMAIL = "ex2@naver.com";
  public static final String EMAIL_RECEIVER_2_NAME = "guest2";

  public static EmailReceiver getEmailReceiverFixture1() {
    return EmailReceiver.builder()
        .email(EMAIL_RECEIVER_1_EMAIL)
        .name(EMAIL_RECEIVER_1_NAME)
        .build();
  }

  public static EmailReceiver getEmailReceiverFixture2() {
    return EmailReceiver.builder()
        .email(EMAIL_RECEIVER_2_EMAIL)
        .name(EMAIL_RECEIVER_2_NAME)
        .build();
  }
}
