package gdsc.konkuk.platformcore.application.member.dtos;

import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceInfo;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MemberAttendanceInfos {
  @NotNull
  private final List<MemberAttendanceInfo> attendanceInfoList = new ArrayList<>();
  @NotEmpty
  private Long totalAttendances;
  @NotEmpty
  private Long actualAttendances;

  public MemberAttendanceInfos() {
    this.totalAttendances = 0L;
    this.actualAttendances = 0L;
  }

  public void addAttendanceInfo(MemberAttendanceInfo attendanceInfo) {
    if (!attendanceInfo.getAttendanceType().isAbsent()) {
      actualAttendances++;
    }
    totalAttendances++;
    attendanceInfoList.add(attendanceInfo);
  }
}
