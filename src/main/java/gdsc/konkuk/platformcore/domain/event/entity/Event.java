package gdsc.konkuk.platformcore.domain.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// TODO: @Notnull, @Notblank와 같은 vaildation에 대한 논의가 이뤄져야 함
// TODO: 값 객체를 나눠야 하는지에 대한 논의가 필요함 (e.g., `startAt`, `endAt`를 `EventPeriod`로 분리)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "thumbnail_url")
  private String thumbnailUrl;

  @Column(name = "start_at")
  private LocalDateTime startAt;

  @Column(name = "end_at")
  private LocalDateTime endAt;

  @Column(name = "has_attendance")
  private boolean hasAttendance;

  @Builder
  public Event(
      Long id,
      String title,
      String description,
      String thumbnailUrl,
      LocalDateTime startAt,
      LocalDateTime endAt,
      boolean hasAttendance) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.thumbnailUrl = thumbnailUrl;
    this.startAt = startAt;
    this.endAt = endAt;
    this.hasAttendance = hasAttendance;
  }

  public void registerAttendance() {
    this.hasAttendance = true;
  }

  public void deleteAttendance() {
    this.hasAttendance = false;
  }
}
