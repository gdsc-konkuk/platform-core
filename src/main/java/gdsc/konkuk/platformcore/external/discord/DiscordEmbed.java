package gdsc.konkuk.platformcore.external.discord;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscordEmbed {

  private String title;
  private String description;

  @Builder
  public DiscordEmbed(String title, String description) {
    this.title = title;
    this.description = description;
  }
}
