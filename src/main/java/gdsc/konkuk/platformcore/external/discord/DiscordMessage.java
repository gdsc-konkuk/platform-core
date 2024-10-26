package gdsc.konkuk.platformcore.external.discord;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.DISCORD_ERROR_DESCRIPTION;
import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.DISCORD_ERROR_TIME_TEXT;
import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.DISCORD_ERROR_TITLE;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscordMessage implements Serializable {

  private final static int MAX_STACK_DEPTH = 2;
  private String content;
  private final List<DiscordEmbed> embeds = new ArrayList<>();

  public DiscordMessage(String content) {
    this.content = content;
  }

  public void addEmbed(DiscordEmbed embed) {
    embeds.add(embed);
  }

  /***
   * Exception을 DiscordMessage로 변환하는 정적 메서드
   * DiscordMessage는 Discord의 Webhook을 통해 메세지를 전송하기 위한 클래스이다.
   * @param e 발생한 Exception
   * @return {@link DiscordMessage} 클래스의 인스턴스를 반환한다.
   * @author ekgns33
   */
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
                            + getReducedStackTrace(e)
                            + "\n```"
            )
            .build();
    ret.addEmbed(embed);
    return ret;
  }

  /***
   * 스택트레이스의 최대 2개의 요소만을 반환한다. 디스코드의 Description에는 2048자가 최대길이이다.
   * <a href="https://discord.com/safety/using-webhooks-and-embeds">디스코드 임베딩 규칙</a>
   *
   * @param e 발생한 Exception
   * @return 최대 2개의 스택트레이스 요소를 concat한 문자열 : String
   * @author ekgns33
   */
  private static String getReducedStackTrace(Exception e) {
    return Arrays.stream(e.getStackTrace())
            .limit(MAX_STACK_DEPTH)
            .map(StackTraceElement::toString)
            .reduce("", (acc, cur) -> acc + "\n" + cur);
  }
}