package gdsc.konkuk.platformcore.util.fixture.attendance;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import gdsc.konkuk.platformcore.domain.attendance.entity.AttendanceType;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ParticipantFixture {

    private final Participant fixture;

    @Builder
    public ParticipantFixture(Long memberId, Attendance attendance, AttendanceType attendanceType) {
        this.fixture = Participant.builder()
                .memberId(getDefault(memberId, 0L))
                .attendance(
                        getDefault(attendance, AttendanceFixture.builder().build().getFixture()))
                .attendanceType(getDefault(attendanceType, AttendanceType.ABSENT))
                .build();
    }
}
