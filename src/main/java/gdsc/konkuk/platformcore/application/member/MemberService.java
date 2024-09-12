package gdsc.konkuk.platformcore.application.member;

import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceQueryDto;
import gdsc.konkuk.platformcore.application.member.dtos.MemberAttendances;
import gdsc.konkuk.platformcore.application.member.exceptions.UserNotAllowedException;
import gdsc.konkuk.platformcore.application.member.exceptions.UserPasswordInvalidException;
import gdsc.konkuk.platformcore.domain.member.entity.MemberRole;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceErrorCode;
import gdsc.konkuk.platformcore.application.attendance.exceptions.ParticipantNotFoundException;
import gdsc.konkuk.platformcore.controller.member.dtos.AttendanceUpdateInfo;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import gdsc.konkuk.platformcore.domain.attendance.repository.AttendanceRepository;
import gdsc.konkuk.platformcore.domain.attendance.repository.ParticipantRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyExistException;
import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;
import gdsc.konkuk.platformcore.controller.member.dtos.MemberRegisterRequest;
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
    return memberRepository.save(MemberRegisterRequest.toEntity(registerRequest));
  }

  @Transactional
  public void changePassword(String memberId, String password) {
    Member member =
        memberRepository
            .findByMemberId(memberId)
            .orElseThrow(() -> UserNotFoundException.of(MemberErrorCode.USER_NOT_FOUND));

    if(!member.isPasswordCorrect("")) { // 비밀번호가 초깃값인지 확인
      throw UserPasswordInvalidException.of(MemberErrorCode.USER_PASSWORD_INVALID);
    }
    if(member.getRole() != MemberRole.ADMIN) { // 우선은 관리자만 비밀번호 변경 허용
      throw UserNotAllowedException.of(MemberErrorCode.USER_NOT_ALLOWED);
    }

    String encodedPassword = passwordEncoder.encode(password);
    member.updatePassword(encodedPassword);
  }

  @Transactional
  public void withdraw(Long currentId) {
    Member member =
        memberRepository
            .findById(currentId)
            .orElseThrow(() -> UserNotFoundException.of(MemberErrorCode.USER_NOT_FOUND));
    member.withdraw();
  }

  public List<MemberAttendances> getMemberAttendanceWithBatchAndPeriod(String batch, LocalDate month) {
    List<MemberAttendanceQueryDto> attendanceInfoList =
        attendanceRepository.findAllAttendanceInfoByBatchAndPeriod(
            batch,
            month.withDayOfMonth(1).atStartOfDay(),
            month.withDayOfMonth(month.lengthOfMonth()).atTime(LocalTime.MAX));
    return MemberAttendances.from(attendanceInfoList);
  }

  @Transactional
  public void updateAttendances(
      String batch, LocalDate month, List<AttendanceUpdateInfo> attendanceUpdateInfoList) {
    Map<Long, Participant> participantMap = fetchParticipants(batch, month);
    updateAttendanceStatuses(participantMap, attendanceUpdateInfoList);
  }

  private void updateAttendanceStatuses(Map<Long, Participant> participants, List<AttendanceUpdateInfo> updateInfos) {
    for (AttendanceUpdateInfo attendanceUpdateInfo : updateInfos) {
      if (!participants.containsKey(attendanceUpdateInfo.getParticipantId())) {
        throw ParticipantNotFoundException.of(AttendanceErrorCode.PARTICIPANT_NOT_FOUND);
      }
      Participant participant = participants.get(attendanceUpdateInfo.getParticipantId());
      participant.updateAttendanceStatus(attendanceUpdateInfo.isAttended());
    }
  }

  private Map<Long, Participant> fetchParticipants(String batch, LocalDate month) {
    return participantRepository
        .findAllByBatchAndStartAtBetween(
            batch,
            month.withDayOfMonth(1).atStartOfDay(),
            month.withDayOfMonth(month.lengthOfMonth()).atTime(LocalTime.MAX))
        .stream()
        .collect(toMap(Participant::getId, identity()));
  }

  private boolean checkMemberExistWithMemberId(String memberId) {
    Optional<Member> member = memberRepository.findByMemberId(memberId);
    return member.isPresent();
  }
}
