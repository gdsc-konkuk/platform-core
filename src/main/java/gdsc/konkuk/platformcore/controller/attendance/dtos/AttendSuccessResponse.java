package gdsc.konkuk.platformcore.controller.attendance.dtos;

import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AttendSuccessResponse {
  private Long id;
  private Long memberId;
  private Long attendanceId;
  private boolean isAttended;

  @Builder
  private AttendSuccessResponse(Long id, Long memberId, Long attendanceId, boolean isAttended) {
    this.id = id;
    this.memberId =memberId;
    this.attendanceId =attendanceId;
    this.isAttended = isAttended;
  }

  public static AttendSuccessResponse from(Participant participant, Long attendanceId) {
    return AttendSuccessResponse.builder()
        .id(participant.getId())
        .memberId(participant.getMemberId())
        .attendanceId(attendanceId)
        .isAttended(participant.isAttended())
        .build();
  }
}
