package gdsc.konkuk.platformcore.domain.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "attendance_id")
  private Attendance attendance;

  @Column(name = "member_id")
  private Long memberId;

  @Column(name = "attendance")
  private boolean isAttended;

  @Builder
  public Participant(Long memberId, Attendance attendance, boolean isAttended) {
    this.memberId = memberId;
    this.attendance = attendance;
    this.isAttended = isAttended;
  }

  public void register(Attendance attendance) {
    if(isAttended || attendance == null) {
      throw new IllegalStateException();
    }
    this.attendance = attendance;
  }

  public void attend() {
    this.isAttended = true;
  }

  public void cancel() {
    this.isAttended = false;
  }

  public void updateAttendanceStatus(boolean isAttended) {
    this.isAttended = isAttended;
  }
}
