package gdsc.konkuk.platformcore.fixture.email;

import static gdsc.konkuk.platformcore.fixture.email.EmailDetailFixture.getEmailDetailFixture;
import static gdsc.konkuk.platformcore.fixture.email.EmailReceiverFixture.getEmailReceiverFixture1;
import static gdsc.konkuk.platformcore.fixture.email.EmailReceiverFixture.getEmailReceiverFixture2;

import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class EmailTaskFixture {
  public static final LocalDateTime EMAIL_TASK_SEND_AT =
      LocalDateTime.of(2024, 9, 1, 20, 0);

  public static final Long EMAIL_TASK_1_ID = 1L;
  public static final Long EMAIL_TASK_2_ID = 2L;
  public static final Long EMAIL_TASK_ALREADY_SENT_ID = 3L;

  public static EmailTask getEmailTaskFixture1() {
    return EmailTask.builder()
        .id(EMAIL_TASK_1_ID)
        .emailDetail(getEmailDetailFixture())
        .receivers(new EmailReceivers(Set.of(
            getEmailReceiverFixture1(),
            getEmailReceiverFixture2())))
        .sendAt(EMAIL_TASK_SEND_AT)
        .build();
  }

  public static EmailTask getEmailTaskFixture2(){
    return EmailTask.builder()
        .id(EMAIL_TASK_2_ID)
        .emailDetail(getEmailDetailFixture())
        .receivers(new EmailReceivers(Set.of(
            getEmailReceiverFixture1(),
            getEmailReceiverFixture2())))
        .sendAt(EMAIL_TASK_SEND_AT)
        .build();
  }

  public static EmailTask getEmailTaskFixtureWillSendAfterXSeconds(int seconds) {
    return EmailTask.builder()
        .id(EMAIL_TASK_1_ID)
        .emailDetail(getEmailDetailFixture())
        .receivers(new EmailReceivers(Set.of(
            getEmailReceiverFixture1(),
            getEmailReceiverFixture2())))
        .sendAt(LocalDateTime.now().plusSeconds(seconds))
        .build();
  }

  public static List<EmailTask> getEmailTaskListFixture() {
    return List.of(
        getEmailTaskFixture1(),
        getEmailTaskFixtureWillSendAfterXSeconds(10),
        getEmailTaskFixtureWillSendAfterXSeconds(20));
  }

  public static EmailTask getEmailTaskAlreadySentFixture(){
    EmailTask emailTask = EmailTask.builder()
        .id(EMAIL_TASK_ALREADY_SENT_ID)
        .emailDetail(getEmailDetailFixture())
        .receivers(new EmailReceivers(Set.of(
            getEmailReceiverFixture1(),
            getEmailReceiverFixture2())))
        .sendAt(LocalDateTime.now().minusSeconds(10))
        .build();
    emailTask.markAsSent();
    return emailTask;
  }
}
