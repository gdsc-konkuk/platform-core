package gdsc.konkuk.platformcore.domain.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  @Column(name = "attendance_type")
  @Enumerated(EnumType.STRING)
  private AttendanceType attendanceType;

  @Builder
  public Participant(Long memberId, Attendance attendance, AttendanceType attendanceType) {
    this.memberId = memberId;
    this.attendance = attendance;
    this.attendanceType = attendanceType;
  }

  public void register(Attendance attendance) {
    if(this.attendance != null || attendance == null) {
      throw new IllegalStateException();
    }
    this.attendance = attendance;
  }

  public boolean isAttend() {
    return !this.attendanceType.equals(AttendanceType.ABSENT);
  }

  public void attend() {
    this.attendanceType = AttendanceType.ATTEND;
  }

  public void updateAttendanceStatus(AttendanceType isAttended) {
    this.attendanceType = isAttended;
  }
}
