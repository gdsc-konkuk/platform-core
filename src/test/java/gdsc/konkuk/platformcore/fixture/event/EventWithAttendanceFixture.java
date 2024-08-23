package gdsc.konkuk.platformcore.fixture.event;

import static gdsc.konkuk.platformcore.fixture.Attendance.AttendanceFixture.ATTENDANCE_ACTIVE_ID;
import static gdsc.konkuk.platformcore.fixture.Attendance.AttendanceFixture.ATTENDANCE_INACTIVE_ID;
import static gdsc.konkuk.platformcore.fixture.event.EventFixture.*;

import gdsc.konkuk.platformcore.application.event.dtos.EventWithAttendance;

public class EventWithAttendanceFixture {
  public static EventWithAttendance getEventWithAttendanceFixture1() {
    return EventWithAttendance.builder()
      .eventId(EVENT_1_ID)
      .attendanceId(ATTENDANCE_ACTIVE_ID)
      .title(EVENT_1_TITLE)
      .startAt(EVENT_1_START_AT)
      .build();
  }

  public static EventWithAttendance getEventWithAttendanceFixture2() {
    return EventWithAttendance.builder()
      .eventId(EVENT_2_ID)
      .attendanceId(ATTENDANCE_INACTIVE_ID)
      .title(EVENT_2_TITLE)
      .startAt(EVENT_2_START_AT)
      .build();
  }

  public static EventWithAttendance getEventWithAttendanceFixture3() {
    return EventWithAttendance.builder()
      .eventId(EVENT_3_ID)
      .attendanceId(null)
      .title(EVENT_3_TITLE)
      .startAt(EVENT_3_START_AT)
      .build();
  }
}
