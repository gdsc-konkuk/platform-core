package gdsc.konkuk.platformcore.domain.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.net.URL;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventImage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_id")
  private Long eventId;

  @Column(name = "url")
  private URL url;

  @Builder
  public EventImage(Long eventId, URL url) {
    this.eventId = eventId;
    this.url = url;
  }

  public boolean isUrlEqual(URL imageKey) {
    return url.equals(imageKey);
  }
}
