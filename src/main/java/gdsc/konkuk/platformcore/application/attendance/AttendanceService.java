package gdsc.konkuk.platformcore.application.attendance;

import static gdsc.konkuk.platformcore.application.attendance.AttendanceServiceHelper.findAttendanceById;

import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceAlreadyExistException;
import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceErrorCode;
import gdsc.konkuk.platformcore.application.event.exceptions.EventErrorCode;
import gdsc.konkuk.platformcore.application.event.exceptions.EventNotFoundException;
import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;
import gdsc.konkuk.platformcore.controller.attendance.dtos.AttendanceRegisterRequest;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import gdsc.konkuk.platformcore.domain.attendance.repository.AttendanceRepository;
import gdsc.konkuk.platformcore.domain.attendance.repository.ParticipantRepository;
import gdsc.konkuk.platformcore.domain.event.entity.Event;
import gdsc.konkuk.platformcore.domain.event.repository.EventRepository;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
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
    Attendance attendance = findAttendanceById(attendanceRepository, attendanceId);
    attendance.validateActiveQr(qrUuid);
    return participantService.attend(member.getId(), attendanceId);
  }

  @Transactional
  public Attendance registerAttendance(AttendanceRegisterRequest registerRequest) {
    checkAttendanceAlreadyExist(registerRequest.getEventId());
    Attendance newAttendance = AttendanceRegisterRequest.toEntity(registerRequest);
    newAttendance.generateQr();
    attendanceRepository.saveAndFlush(newAttendance);

    List<Member> members = memberRepository.findAllByBatch(registerRequest.getBatch());
    registerParticipants(newAttendance, members);
    return newAttendance;
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

  private void checkAttendanceAlreadyExist(Long eventId) {
    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(() -> EventNotFoundException.of(EventErrorCode.EVENT_NOT_FOUND));

    attendanceRepository
        .findByEventId(event.getId())
        .ifPresent(
            attendance -> {
              throw AttendanceAlreadyExistException.of(
                  AttendanceErrorCode.ATTENDANCE_ALREADY_EXIST);
            });
  }

  private void registerParticipants(Attendance attendance, List<Member> members){
    List<Participant> participants = new ArrayList<>();
    for(Member member : members) {
      Participant participant = Participant.builder()
          .memberId(member.getId())
          .isAttended(false)
          .build();
      participant.register(attendance);
      participants.add(participant);
    }
    participantRepository.saveAll(participants);
  }
}
