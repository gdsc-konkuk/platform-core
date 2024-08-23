package gdsc.konkuk.platformcore.fixture.Attendance;

import static gdsc.konkuk.platformcore.fixture.event.EventFixture.*;

import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;

public class AttendanceFixture {
  public static final Long ATTENDANCE_ACTIVE_ID = 1L;
  public static final Long ATTENDANCE_ACTIVE_EVENT_ID = EVENT_1_ID;
  public static final String ATTENDANCE_ACTIVE_ACTIVE_QR_UUID = "example";

  public static final Long ATTENDANCE_INACTIVE_ID = 2L;
  public static final Long ATTENDANCE_INACTIVE_EVENT_ID = EVENT_2_ID;
  public static final String ATTENDANCE_INACTIVE_ACTIVE_QR_UUID = null;

  public static final Long ATTENDANCE_EMPTY_ID = 3L;

  public static Attendance getAttendanceFixtureActive(){
    return Attendance.builder()
      .id(ATTENDANCE_ACTIVE_ID)
      .eventId(ATTENDANCE_ACTIVE_EVENT_ID)
      .activeQrUuid(ATTENDANCE_ACTIVE_ACTIVE_QR_UUID)
      .build();
  }

  public static Attendance getAttendanceFixtureInactive(){
    return Attendance.builder()
      .id(ATTENDANCE_INACTIVE_ID)
      .eventId(ATTENDANCE_INACTIVE_EVENT_ID)
      .activeQrUuid(ATTENDANCE_INACTIVE_ACTIVE_QR_UUID)
      .build();
  }
}
