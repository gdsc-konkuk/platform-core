package gdsc.konkuk.platformcore.controller.event.dtos;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EventUpdateRequest {
  private String title;
  private String content;
  private String location;
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private List<URL> eventImagesToDelete;
}
