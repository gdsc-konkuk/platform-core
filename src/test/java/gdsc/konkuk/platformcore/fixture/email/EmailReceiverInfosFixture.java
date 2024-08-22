package gdsc.konkuk.platformcore.fixture.email;

import static gdsc.konkuk.platformcore.fixture.email.EmailReceiverFixture.*;

import gdsc.konkuk.platformcore.controller.email.dtos.EmailReceiverInfo;

public class EmailReceiverInfosFixture {
  public static EmailReceiverInfo getEmailReceiverInfoFixture1() {
    return EmailReceiverInfo.builder()
        .email(EMAIL_RECEIVER_1_EMAIL)
        .name(EMAIL_RECEIVER_1_NAME)
        .build();
  }

  public static EmailReceiverInfo getEmailReceiverInfoFixture2() {
    return EmailReceiverInfo.builder()
        .email(EMAIL_RECEIVER_2_EMAIL)
        .name(EMAIL_RECEIVER_2_NAME)
        .build();
  }
}
