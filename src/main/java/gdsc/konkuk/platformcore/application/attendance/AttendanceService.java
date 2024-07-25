package gdsc.konkuk.platformcore.application.attendance;

import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceErrorCode;
import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceNotFoundException;
import gdsc.konkuk.platformcore.application.attendance.exceptions.QrInvalidException;
import gdsc.konkuk.platformcore.application.event.exceptions.EventErrorCode;
import gdsc.konkuk.platformcore.application.event.exceptions.EventNotFoundException;
import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;
import gdsc.konkuk.platformcore.controller.attendance.AttendanceRegisterRequest;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import gdsc.konkuk.platformcore.domain.attendance.repository.AttendanceRepository;
import gdsc.konkuk.platformcore.domain.attendance.repository.ParticipantRepository;
import gdsc.konkuk.platformcore.domain.event.repository.EventRepository;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {
  private final ParticipantRepository participantRepository;
  private final EventRepository eventRepository;
  private final MemberRepository memberRepository;
  private final AttendanceRepository attendanceRepository;
  private final ParticipantService participantService;

  @Transactional
  public Participant attend(String memberEmail, Long attendanceId, String qrUuid) {
    Member member =
      memberRepository
        .findByEmail(memberEmail)
        .orElseThrow(() -> UserNotFoundException.of(MemberErrorCode.USER_NOT_FOUND));
    Attendance attendance =
      attendanceRepository
        .findById(attendanceId)
        .orElseThrow(
          () -> AttendanceNotFoundException.of(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));
    if (!attendance.isActiveQr(qrUuid)) {
      throw QrInvalidException.of(AttendanceErrorCode.INVALID_QR_UUID);
    }

    return participantService.attend(member.getId(), attendanceId);
  }

  @Transactional
  public Long registerAttendance(AttendanceRegisterRequest registerRequest) {
    checkEventExist(registerRequest.getEventId());

    Attendance newAttendance = AttendanceRegisterRequest.toEntity(registerRequest);
    attendanceRepository.saveAndFlush(newAttendance);

    List<Member> members = memberRepository.findAllByBatch(registerRequest.getBatch());
    List<Participant> participants =
      MemberToParticipantMapper.mapMemberListToParticipantList(
        members, newAttendance.getId(), false);
    participantRepository.saveAll(participants);

    return newAttendance.getId();
  }

  @Transactional
  public void deleteAttendance(Long attendanceId) {
    participantRepository.deleteAllByAttendanceId(attendanceId);
    attendanceRepository.deleteById(attendanceId);
  }

  @Transactional
  public String generateQr(Long attendanceId) {
    Attendance attendance =
      attendanceRepository
        .findById(attendanceId)
        .orElseThrow(
          () -> AttendanceNotFoundException.of(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));
    return attendance.generateQr();
  }

  @Transactional
  public void expireQr(Long attendanceId, String qrUuid) {
    Attendance attendance =
      attendanceRepository
        .findById(attendanceId)
        .orElseThrow(
          () -> AttendanceNotFoundException.of(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));
    if (!attendance.isActiveQr(qrUuid)) {
      throw QrInvalidException.of(AttendanceErrorCode.INVALID_QR_UUID);
    }

    attendance.expireQr();
  }

  private void checkEventExist(Long eventId) {
    eventRepository
      .findById(eventId)
      .orElseThrow(() -> EventNotFoundException.of(EventErrorCode.EVENT_NOT_FOUND));
  }
}
