package gdsc.konkuk.platformcore.fixture.email;

import static gdsc.konkuk.platformcore.fixture.email.EmailDetailFixture.*;
import static gdsc.konkuk.platformcore.fixture.email.EmailReceiverInfosFixture.getEmailReceiverInfoFixture1;
import static gdsc.konkuk.platformcore.fixture.email.EmailReceiverInfosFixture.getEmailReceiverInfoFixture2;
import static gdsc.konkuk.platformcore.fixture.email.EmailTaskFixture.*;

import gdsc.konkuk.platformcore.controller.email.dtos.EmailSendRequest;
import java.time.LocalDateTime;
import java.util.Set;

public class EmailSendRequestFixture {
  public static EmailSendRequest getEmailTask1SendRequestFixture() {
    return EmailSendRequest.builder()
        .subject(EMAIL_SUBJECT)
        .content(EMAIL_CONTENT)
        .sendAt(EMAIL_TASK_SEND_AT)
        .receiverInfos(Set.of(getEmailReceiverInfoFixture1(), getEmailReceiverInfoFixture2()))
        .build();
  }

  public static EmailSendRequest getEmailTask2SendRequestFixture() {
    return EmailSendRequest.builder()
        .subject(EMAIL_SUBJECT)
        .content(EMAIL_CONTENT)
        .sendAt(EMAIL_TASK_SEND_AT)
        .receiverInfos(Set.of(getEmailReceiverInfoFixture1(), getEmailReceiverInfoFixture2()))
        .build();
  }

  public static EmailSendRequest getEmailSendRequestWillSendAfterXSeconds(int seconds) {
    return EmailSendRequest.builder()
        .subject(EMAIL_SUBJECT)
        .content(EMAIL_CONTENT)
        .sendAt(LocalDateTime.now().plusSeconds(seconds))
        .receiverInfos(Set.of(getEmailReceiverInfoFixture1(), getEmailReceiverInfoFixture2()))
        .build();
  }
}
