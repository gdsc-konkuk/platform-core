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
public class Participant {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "attendance_id")
  private Long attendanceId;

  @Column(name = "member_id")
  private Long memberId;

  @Column(name = "attendance")
  private boolean attendance;

  @Builder
  public Participant(Long id, Long attendanceId, Long memberId, boolean attendance) {
    this.id = id;
    this.attendanceId = attendanceId;
    this.memberId = memberId;
    this.attendance = attendance;
  }

  public void attend() {
    this.attendance = true;
  }
}
