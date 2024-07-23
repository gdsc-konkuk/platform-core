package gdsc.konkuk.platformcore.application.attendance;

import gdsc.konkuk.platformcore.application.event.exceptions.EventErrorCode;
import gdsc.konkuk.platformcore.application.event.exceptions.EventNotFoundException;
import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participants;
import gdsc.konkuk.platformcore.domain.attendance.repository.ParticipantsRepository;
import gdsc.konkuk.platformcore.domain.event.entity.Event;
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
  private final ParticipantsRepository participantsRepository;
  private final EventRepository eventRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public void registerAttendance(Long eventId) {
    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(() -> EventNotFoundException.of(EventErrorCode.EVENT_NOT_FOUND));

    List<Participants> participants =
        memberRepository.findAll().stream()
            .map(
                member ->
                    Participants.builder()
                        .eventId(event.getId())
                        .memberId(member.getId())
                        .attendance(false)
                        .build())
            .toList();
    participantsRepository.saveAll(participants);

    event.registerAttendance();
    eventRepository.save(event);
  }

  @Transactional
  public void deleteAttendance(Long eventId) {
    Event event = eventRepository.findById(eventId).orElseThrow();
    participantsRepository.deleteAllByEventId(eventId);
    event.deleteAttendance();
    eventRepository.save(event);
  }

  @Transactional
  public Participants attend(String memberEmail, Long eventId) {
    Member member =
        memberRepository
            .findByEmail(memberEmail)
            .orElseThrow(() -> UserNotFoundException.of(MemberErrorCode.USER_NOT_FOUND));
    Participants participants =
        participantsRepository
            .findByMemberIdAndEventId(member.getId(), eventId)
            .orElseThrow(() -> EventNotFoundException.of(EventErrorCode.EVENT_NOT_FOUND));
    participants.attend();
    return participantsRepository.save(participants);
  }
}
