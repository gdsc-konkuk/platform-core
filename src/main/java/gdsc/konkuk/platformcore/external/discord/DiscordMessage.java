package gdsc.konkuk.platformcore.external.discord;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscordMessage {

  private String content;
  private List<DiscordEmbed> embeds = new ArrayList<>();

  public DiscordMessage(String content){
    this.content = content;
  }

  public void addEmbed(DiscordEmbed embed){
    embeds.add(embed);
  }
}
