package gdsc.konkuk.platformcore.application.attendance;

import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceErrorCode;
import gdsc.konkuk.platformcore.application.attendance.exceptions.ParticipantNotFoundException;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import gdsc.konkuk.platformcore.domain.attendance.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService {
  private final ParticipantRepository participantRepository;

  @Transactional
  public Participant attend(Long memberId, Long attendanceId) {
    Participant participant =
        participantRepository
            .findByMemberIdAndAttendanceId(memberId, attendanceId)
            .orElseThrow(
                () -> ParticipantNotFoundException.of(AttendanceErrorCode.PARTICIPANT_NOT_FOUND));

    participant.attend();
    return participant;
  }
}
