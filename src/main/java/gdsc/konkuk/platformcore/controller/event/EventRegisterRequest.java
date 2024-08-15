package gdsc.konkuk.platformcore.controller.event;

import gdsc.konkuk.platformcore.domain.event.entity.Event;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventRegisterRequest {
  @NotEmpty private String title;
  @NotNull private String content;
  @NotNull private String location;
  @NotNull private LocalDateTime startAt;
  @NotNull private LocalDateTime endAt;

  public static Event toEntity(EventRegisterRequest request) {
    return Event.builder()
        .title(request.getTitle())
        .content(request.getContent())
        .location(request.getLocation())
        .startAt(request.getStartAt())
        .endAt(request.getEndAt())
        .retrospectContent("내용이 없습니다.")
        .build();
  }
}
