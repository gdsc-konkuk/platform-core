package gdsc.konkuk.platformcore.application.member;

import gdsc.konkuk.platformcore.application.attendance.AttendanceInfo;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MemberAttendanceInfo {
  @NotNull private Long memberId;
  @NotEmpty private String memberName;
  @NotNull private MemberRole memberRole;
  private String profileImageUrl;
  @NotEmpty private String department;
  @NotNull List<AttendanceInfo> attendanceInfoList;

  public static List<MemberAttendanceInfo> from(
      List<Member> batchMemberList, List<AttendanceInfo> attendanceInfoList) {
    Map<Long, MemberAttendanceInfo> memberAttendanceInfoMap = new HashMap<>();
    for (Member member : batchMemberList) {
      memberAttendanceInfoMap.put(
          member.getId(),
          MemberAttendanceInfo.builder()
              .memberId(member.getId())
              .memberName(member.getName())
              .memberRole(member.getRole())
              .profileImageUrl(member.getProfileImageUrl())
              .department(member.getDepartment())
              .attendanceInfoList(new ArrayList<>())
              .build());
    }
    for (AttendanceInfo attendanceInfo : attendanceInfoList) {
      if (!memberAttendanceInfoMap.containsKey(attendanceInfo.getMemberId())) continue;
      MemberAttendanceInfo memberAttendanceInfo =
          memberAttendanceInfoMap.get(attendanceInfo.getMemberId());
      memberAttendanceInfo.getAttendanceInfoList().add(attendanceInfo);
    }
    return memberAttendanceInfoMap.values().stream().toList();
  }
}
