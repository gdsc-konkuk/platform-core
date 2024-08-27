package gdsc.konkuk.platformcore.domain.attendance.entity;

import static org.junit.jupiter.api.Assertions.*;

import gdsc.konkuk.platformcore.application.attendance.exceptions.QrInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AttendanceTest {

  @Test
  @DisplayName("QR코드가 null이거나 불일치하면 실패")
  void should_fail_when_Qr_expired() {
    //given
    Attendance attendance = Attendance.builder()
        .activeQrUuid("example")
        .build();

    // when, then
    assertThrows(QrInvalidException.class,
        () -> attendance.validateActiveQr("example2"));
  }

  @Test
  @DisplayName("QR코드를 expire했을 때, 기존 QR은 validation 실패")
  void should_fail_when_qr_expired() {
    // given
    Attendance attendance = Attendance.builder()
        .activeQrUuid("example")
        .build();

    // when
    attendance.expireQr();

    // then
    assertThrows(QrInvalidException.class,
        () -> attendance.validateActiveQr("example")
    );
  }
}
