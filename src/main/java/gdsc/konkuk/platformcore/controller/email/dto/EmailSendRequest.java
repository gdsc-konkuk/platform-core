package gdsc.konkuk.platformcore.controller.email.dto;

import java.util.List;

import gdsc.konkuk.platformcore.domain.email.entity.PlatformEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailSendRequest {
  @NotEmpty
  private String subject;
  @NotEmpty
  private String content;
  @NotNull
  private List<@Email String> receivers;

  @Builder
  public EmailSendRequest(String subject, String content, List<String> receivers) {
    this.subject = subject;
    this.content = content;
    this.receivers = receivers;
  }

  public static PlatformEmail toEntity(EmailSendRequest request) {
    return PlatformEmail.builder()
      .content(request.getContent())
      .subject(request.getSubject())
      .build();
  }
}