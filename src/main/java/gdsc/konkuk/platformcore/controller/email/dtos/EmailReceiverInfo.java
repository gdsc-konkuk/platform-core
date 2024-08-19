package gdsc.konkuk.platformcore.controller.email.dtos;

import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailReceiverInfo {
  @NotEmpty @Email
  private String email;

  @NotEmpty private String name;

  @Builder
  public EmailReceiverInfo(String email, String name) {
    this.email = email;
    this.name = name;
  }

  public static EmailReceiver toValueObject(EmailReceiverInfo info) {
    return EmailReceiver.builder()
      .email(info.getEmail())
      .name(info.getName())
      .build();
  }

  public static EmailReceiverInfo fromValueObject(EmailReceiver emailReceiver) {
    return EmailReceiverInfo.builder()
      .email(emailReceiver.getEmail())
      .name(emailReceiver.getName())
      .build();
  }
}
