package gdsc.konkuk.platformcore.application.member;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import gdsc.konkuk.platformcore.application.attendance.dtos.MemberAttendanceQueryDto;
import gdsc.konkuk.platformcore.application.attendance.exceptions.AttendanceErrorCode;
import gdsc.konkuk.platformcore.application.attendance.exceptions.ParticipantNotFoundException;
import gdsc.konkuk.platformcore.application.member.dtos.MemberAttendances;
import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyExistException;
import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;
import gdsc.konkuk.platformcore.controller.member.dtos.AttendanceUpdateInfo;
import gdsc.konkuk.platformcore.controller.member.dtos.MemberRegisterRequest;
import gdsc.konkuk.platformcore.controller.member.dtos.MemberUpdateInfo;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import gdsc.konkuk.platformcore.domain.attendance.repository.AttendanceRepository;
import gdsc.konkuk.platformcore.domain.attendance.repository.ParticipantRepository;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
    private final ParticipantRepository participantRepository;

    public List<Member> getMembersInBatch(String batch) {
        return memberRepository.findAllActiveByBatch(batch);
    }

    @Transactional
    public Member register(MemberRegisterRequest registerRequest) {
        if (checkMemberExistWithStudentId(registerRequest.getStudentId())) {
            throw UserAlreadyExistException.of(MemberErrorCode.USER_ALREADY_EXISTS);
        }
        return memberRepository.save(MemberRegisterRequest.toEntity(registerRequest));
    }

    @Transactional
    public void withdraw(Long currentId) {
        Member member =
                memberRepository
                        .findById(currentId)
                        .orElseThrow(
                                () -> UserNotFoundException.of(MemberErrorCode.USER_NOT_FOUND));
        member.withdraw();
    }

    @Transactional
    public void updateMembers(String batch, @Valid List<MemberUpdateInfo> updateInfos) {
        List<Long> memberIds = updateInfos.stream().map(MemberUpdateInfo::getMemberId).toList();
        Map<Long, Member> memberMap = fetchMembers(memberIds, batch);
        updateMembers(memberMap, updateInfos);
    }

    public List<MemberAttendances> getMemberAttendanceWithBatchAndPeriod(String batch,
            LocalDate month) {
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

    private void updateMembers(Map<Long, Member> memberMap, List<MemberUpdateInfo> updateInfos) {
        for (MemberUpdateInfo memberUpdateInfo : updateInfos) {
            if (!memberMap.containsKey(memberUpdateInfo.getMemberId())) {
                throw UserNotFoundException.of(MemberErrorCode.USER_NOT_FOUND);
            }
            Member member = memberMap.get(memberUpdateInfo.getMemberId());
            member.update(memberUpdateInfo.toCommand());
        }
    }

    private Map<Long, Member> fetchMembers(List<Long> memberIds, String batch) {
        List<Member> members = memberRepository.findAllByIdsAndBatch(memberIds, batch);
        if (members.size() != memberIds.size()) {
            throw UserNotFoundException.of(MemberErrorCode.USER_NOT_FOUND);
        }
        return members.stream().collect(toMap(Member::getId, identity()));
    }

    private void updateAttendanceStatuses(Map<Long, Participant> participants,
            List<AttendanceUpdateInfo> updateInfos) {
        for (AttendanceUpdateInfo attendanceUpdateInfo : updateInfos) {
            if (!participants.containsKey(attendanceUpdateInfo.getParticipantId())) {
                throw ParticipantNotFoundException.of(AttendanceErrorCode.PARTICIPANT_NOT_FOUND);
            }
            Participant participant = participants.get(attendanceUpdateInfo.getParticipantId());
            participant.updateAttendanceStatus(attendanceUpdateInfo.getAttendanceType());
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

    private boolean checkMemberExistWithStudentId(String studentId) {
        Optional<Member> member = memberRepository.findByStudentId(studentId);
        return member.isPresent();
    }
}
