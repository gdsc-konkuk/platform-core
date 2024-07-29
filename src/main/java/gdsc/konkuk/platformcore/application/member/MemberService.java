package gdsc.konkuk.platformcore.application.member;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import gdsc.konkuk.platformcore.application.attendance.AttendanceInfo;
import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceErrorCode;
import gdsc.konkuk.platformcore.application.attendance.exceptions.ParticipantNotFoundException;
import gdsc.konkuk.platformcore.controller.member.AttendanceUpdateInfo;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import gdsc.konkuk.platformcore.domain.attendance.repository.AttendanceRepository;
import gdsc.konkuk.platformcore.domain.attendance.repository.ParticipantRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyExistException;
import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;
import gdsc.konkuk.platformcore.controller.member.MemberRegisterRequest;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final PasswordEncoder passwordEncoder;
  private final MemberRepository memberRepository;
  private final AttendanceRepository attendanceRepository;
  private final ParticipantRepository participantRepository;

  @Transactional
  public Member register(MemberRegisterRequest registerRequest) {

    if (checkMemberExistWithMemberId(registerRequest.getMemberId())) {
      throw UserAlreadyExistException.of(MemberErrorCode.USER_ALREADY_EXISTS);
    }

    String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
    registerRequest.setPassword(encodedPassword);

    return memberRepository.save(MemberRegisterRequest.toEntity(registerRequest));
  }

  @Transactional
  public void withdraw(Long currentId) {
    Member member =
        memberRepository
            .findById(currentId)
            .orElseThrow(() -> UserNotFoundException.of(MemberErrorCode.USER_NOT_FOUND));
    member.withdraw();
  }

  public List<MemberAttendanceInfo> getMemberAttendanceInfo(String batch, LocalDate month) {
    List<Member> batchMemberList = memberRepository.findAllByBatch(batch);
    List<AttendanceInfo> attendanceInfoList =
        attendanceRepository.findAllAttendanceInfoByStartAtBetween(
            month.withDayOfMonth(1).atStartOfDay(),
            month.withDayOfMonth(month.lengthOfMonth()).atTime(LocalTime.MAX));
    return MemberAttendanceInfo.from(batchMemberList, attendanceInfoList);
  }

  @Transactional
  public void updateAttendances(
      String batch, LocalDate month, List<AttendanceUpdateInfo> attendanceUpdateInfoList) {
    Map<Long, Participant> participantMap =
        participantRepository
            .findAllByBatchAndStartAtBetween(
                batch,
                month.withDayOfMonth(1).atStartOfDay(),
                month.withDayOfMonth(month.lengthOfMonth()).atTime(LocalTime.MAX))
            .stream()
            .collect(toMap(Participant::getId, identity()));

    for (AttendanceUpdateInfo attendanceUpdateInfo : attendanceUpdateInfoList) {
      if (!participantMap.containsKey(attendanceUpdateInfo.getParticipantId())) {
        throw ParticipantNotFoundException.of(AttendanceErrorCode.PARTICIPANT_NOT_FOUND);
      }

      Participant participant = participantMap.get(attendanceUpdateInfo.getParticipantId());
      participant.updateAttendance(attendanceUpdateInfo.isAttendance());
    }
  }

  private boolean checkMemberExistWithMemberId(String memberId) {
    Optional<Member> member = memberRepository.findByMemberId(memberId);
    return member.isPresent();
  }
}
