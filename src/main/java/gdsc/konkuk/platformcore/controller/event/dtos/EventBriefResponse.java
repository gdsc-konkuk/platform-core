package gdsc.konkuk.platformcore.controller.event.dtos;

import gdsc.konkuk.platformcore.application.event.dtos.EventBrief;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EventBriefResponse {
  private List<EventBrief> eventBriefs;
}
