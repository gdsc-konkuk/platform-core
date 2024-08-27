package gdsc.konkuk.platformcore.external.discord;

import java.net.URI;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordClient {

  @Value("${discord.webhook.url}")
  private static String WEB_HOOK_URL;

  private final RestTemplate restTemplate;

  public void sendErrorMessage(Exception e) {
    DiscordMessage message = DiscordMessage.of(e);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<DiscordMessage> entity = new HttpEntity<>(message, headers);
    try {
      restTemplate.postForObject(URI.create(WEB_HOOK_URL), entity, String.class);
    } catch (Exception ex) {
      log.error(Arrays.toString(ex.getStackTrace()));
    }
  }
}
