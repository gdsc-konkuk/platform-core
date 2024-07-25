package gdsc.konkuk.platformcore.application.attendance;

import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import gdsc.konkuk.platformcore.domain.member.entity.Member;

import java.util.List;

public class MemberToParticipantMapper {
  public static List<Participant> mapMemberListToParticipantList(
      List<Member> members, Long attendanceId, boolean attendance) {
    return members.stream()
        .map(
            member ->
                Participant.builder()
                    .attendanceId(attendanceId)
                    .memberId(member.getId())
                    .attendance(attendance)
                    .build())
        .toList();
  }
}
