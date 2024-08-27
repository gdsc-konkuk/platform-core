package gdsc.konkuk.platformcore.fixture.email;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailReceiverFixture {
  private final EmailReceiver fixture;

  @Builder
  public EmailReceiverFixture(String email, String name) {
    this.fixture = EmailReceiver.builder()
      .email(getDefault(email, "ex@gmail.com"))
      .name(getDefault(name, "guest"))
      .build();
  }
}
