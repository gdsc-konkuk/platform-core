package gdsc.konkuk.platformcore.fixture.email;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.controller.email.dtos.EmailReceiverInfo;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailReceiverInfosFixture {
  private final Set<EmailReceiverInfo> fixture;

  @Builder
  public EmailReceiverInfosFixture(Set<EmailReceiverInfo> emailReceiverInfos) {
    this.fixture = getDefault(emailReceiverInfos, Set.of(
        EmailReceiverInfo.builder()
          .email("ex1@gmail.com")
          .name("guest1")
          .build(),
        EmailReceiverInfo.builder()
          .email("ex2@gmail.com")
          .name("guest2")
          .build()));
  }
}
