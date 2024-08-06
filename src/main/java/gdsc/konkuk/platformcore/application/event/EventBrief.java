package gdsc.konkuk.platformcore.application.event;

import java.net.URL;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EventBrief {
  private Long id;
  private String title;
  private String content;
  private LocalDateTime startAt;
  private URL thumbnail;
}
