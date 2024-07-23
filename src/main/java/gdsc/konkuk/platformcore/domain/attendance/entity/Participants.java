package gdsc.konkuk.platformcore.domain.attendance.entity;

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
public class Participants {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "member_id")
  private Long memberId;

  @Column(name = "event_id")
  private Long eventId;

  @Column(name = "attendance")
  private boolean attendance;

  @Builder
  public Participants(Long id, Long memberId, Long eventId, boolean attendance) {
    this.id = id;
    this.memberId = memberId;
    this.eventId = eventId;
    this.attendance = attendance;
  }

  public void attend() {
    this.attendance = true;
  }
}
