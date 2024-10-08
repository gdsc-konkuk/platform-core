package gdsc.konkuk.platformcore.domain.attendance.entity;

import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceErrorCode;
import gdsc.konkuk.platformcore.application.attendance.exceptions.QrInvalidException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_id")
  private Long eventId;

  @Column(name = "active_qr_uuid")
  private String activeQrUuid;

  @Builder
  public Attendance(Long id, Long eventId, String activeQrUuid) {
    this.id = id;
    this.eventId = eventId;
    this.activeQrUuid = activeQrUuid;
  }

  public void validateActiveQr(String qrUuid) {
    if (this.activeQrUuid == null || !this.activeQrUuid.equals(qrUuid))
      throw QrInvalidException.of(AttendanceErrorCode.INVALID_QR_UUID);
  }

  public String generateQr() {
    this.activeQrUuid = UUID.randomUUID().toString();
    return this.activeQrUuid;
  }

  public void expireQr() {
    this.activeQrUuid = null;
  }
}
