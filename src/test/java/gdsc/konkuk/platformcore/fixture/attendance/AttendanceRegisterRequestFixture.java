package gdsc.konkuk.platformcore.fixture.attendance;

import static gdsc.konkuk.platformcore.global.utils.GetDefault.getDefault;

import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceRegisterRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AttendanceRegisterRequestFixture {

    private final AttendanceRegisterRequest fixture;

    @Builder
    public AttendanceRegisterRequestFixture(String title, String batch) {
        this.fixture = AttendanceRegisterRequest.builder()
                .title(getDefault(title, "title"))
                .batch(getDefault(batch, "batch"))
                .build();
    }
}
