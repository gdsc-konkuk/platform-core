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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventImage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_id")
  private Long eventId;

  @Column(name = "object_key")
  private String objectKey;

  @Builder
  public EventImage(Long eventId, String objectKey) {
    this.eventId = eventId;
    this.objectKey = objectKey;
  }

  public boolean equals(String objectKey) {
    return this.objectKey.equals(objectKey);
  }
}
