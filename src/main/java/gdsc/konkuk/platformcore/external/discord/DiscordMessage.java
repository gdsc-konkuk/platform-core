package gdsc.konkuk.platformcore.external.discord;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.DISCORD_ERROR_DESCRIPTION;
import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.DISCORD_ERROR_TIME_TEXT;
import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.DISCORD_ERROR_TITLE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

  public static DiscordMessage of(Exception e) {
    DiscordMessage ret = new DiscordMessage(DISCORD_ERROR_TITLE);
    DiscordEmbed embed = DiscordEmbed.builder()
        .title(e.getMessage())
        .description(
            DISCORD_ERROR_DESCRIPTION
                + "\n"
                + DISCORD_ERROR_TIME_TEXT
                + LocalDateTime.now()
                + "\n"
                + "```\n"
                + Arrays.toString(e.getStackTrace())
                + "\n```"
        )
        .build();
    ret.addEmbed(embed);
    return ret;
  }
}
