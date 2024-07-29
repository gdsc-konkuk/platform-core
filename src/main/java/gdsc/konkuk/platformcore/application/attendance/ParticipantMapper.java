package gdsc.konkuk.platformcore.application.attendance;

import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import gdsc.konkuk.platformcore.domain.member.entity.Member;

import java.util.List;

public class ParticipantMapper {
  public static List<Participant> mapMemberListToAbsentParticipantList(
      List<Member> members, Long attendanceId) {
    return members.stream()
        .map(
            member ->
                Participant.builder()
                    .attendanceId(attendanceId)
                    .memberId(member.getId())
                    .attendance(false)
                    .build())
        .toList();
  }
}
