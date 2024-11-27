package gdsc.konkuk.platformcore.application.attendance;

import static gdsc.konkuk.platformcore.application.attendance.AttendanceServiceHelper.findAttendanceById;

import gdsc.konkuk.platformcore.application.attendance.dtos.AttendanceStatus;
import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import gdsc.konkuk.platformcore.domain.attendance.entity.AttendanceType;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import gdsc.konkuk.platformcore.domain.attendance.repository.AttendanceRepository;
import gdsc.konkuk.platformcore.domain.attendance.repository.ParticipantRepository;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {
  private final ParticipantRepository participantRepository;
  private final MemberRepository memberRepository;
  private final AttendanceRepository attendanceRepository;
  private final ParticipantService participantService;

  @Transactional
  public Participant attend(Long memberId, Long attendanceId, String qrUuid) {
    Member member =
        memberRepository
            .findById(memberId)
            .orElseThrow(() -> UserNotFoundException.of(MemberErrorCode.USER_NOT_FOUND));
    Attendance attendance = findAttendanceById(attendanceRepository, attendanceId);
    attendance.validateActiveQr(qrUuid);
    return participantService.attend(member.getId(), attendanceId);
  }

  public List<Attendance> getAllByPeriod(LocalDate month) {
    return attendanceRepository.findAllByPeriod(month.atStartOfDay(), month.atStartOfDay().plusMonths(1));
  }

  @Transactional
  public Attendance registerAttendance(String title, String batch) {
    Attendance newAttendance = Attendance.builder().title(title).attendanceTime(LocalDateTime.now()).build();
    newAttendance.generateQr();
    attendanceRepository.saveAndFlush(newAttendance);

    List<Member> members = memberRepository.findAllByBatch(batch);
    registerParticipants(newAttendance, members);
    return newAttendance;
  }

  public AttendanceStatus getAttendanceStatus(Long attendanceId) {
    List<Participant> totalParticipants = participantRepository.findAllByAttendanceId(attendanceId);
    List<Participant> attendedParticipants = totalParticipants.stream().filter(Participant::isAttend).toList();
    return AttendanceStatus.of(attendanceId, totalParticipants.size(), attendedParticipants.size());
  }

  @Transactional
  public void deleteAttendance(Long attendanceId) {
    participantRepository.deleteAllByAttendanceId(attendanceId);
    attendanceRepository.deleteById(attendanceId);
  }

  @Transactional
  public Attendance generateQr(Long attendanceId) {
    Attendance attendance = findAttendanceById(attendanceRepository, attendanceId);
    attendance.generateQr();
    return attendance;
  }

  @Transactional
  public void expireQr(Long attendanceId) {
    Attendance attendance = findAttendanceById(attendanceRepository, attendanceId);
    attendance.expireQr();
  }

  private void registerParticipants(Attendance attendance, List<Member> members){
    List<Participant> participants = new ArrayList<>();
    for(Member member : members) {
      Participant participant = Participant.builder()
          .memberId(member.getId())
          .attendanceType(AttendanceType.ABSENT)
          .build();
      participant.register(attendance);
      participants.add(participant);
    }
    participantRepository.saveAll(participants);
  }
}
