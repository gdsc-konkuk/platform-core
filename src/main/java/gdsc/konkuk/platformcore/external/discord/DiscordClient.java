package gdsc.konkuk.platformcore.external.discord;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class DiscordClient {

  @Value("${discord.webhook.url}")
  private static String WEB_HOOK_URL;

  private final RestTemplate restTemplate;

  public void sendErrorMessage(Exception e) {
    DiscordMessage message = createErrorMessage(DISCORD_ERROR_TITLE, DISCORD_ERROR_DESCRIPTION, e);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<DiscordMessage> entity = new HttpEntity<>(message, headers);
    restTemplate.postForObject(WEB_HOOK_URL, entity, String.class);
  }

  private DiscordMessage createErrorMessage(String content, String title, Exception e) {
    DiscordMessage message = new DiscordMessage(content);
    DiscordEmbed embed = DiscordEmbed.builder()
        .title(title)
        .description(
            DISCORD_ERROR_TIME_TEXT
                + LocalDateTime.now()
                + "\n"
                + "```\n"
                + Arrays.toString(e.getStackTrace())
                + "\n```"
        )
        .build();
    message.addEmbed(embed);
    return message;
  }

}
