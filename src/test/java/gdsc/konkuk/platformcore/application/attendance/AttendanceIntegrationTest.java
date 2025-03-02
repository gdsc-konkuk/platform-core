package gdsc.konkuk.platformcore.application.attendance;

import gdsc.konkuk.platformcore.util.fixture.attendance.AttendanceFixture;
import java.time.LocalDateTime;
import java.util.List;
import gdsc.konkuk.platformcore.application.attendance.dtos.AttendanceStatus;
import gdsc.konkuk.platformcore.application.attendance.exceptions.QrInvalidException;
import gdsc.konkuk.platformcore.application.member.MemberService;
import gdsc.konkuk.platformcore.application.member.exceptions.UserNotFoundException;
import gdsc.konkuk.platformcore.domain.attendance.entity.Attendance;
import gdsc.konkuk.platformcore.domain.attendance.entity.AttendanceType;
import gdsc.konkuk.platformcore.domain.attendance.entity.Participant;
import gdsc.konkuk.platformcore.domain.attendance.repository.AttendanceRepository;
import gdsc.konkuk.platformcore.domain.attendance.repository.ParticipantRepository;
import gdsc.konkuk.platformcore.domain.member.entity.Member;
import gdsc.konkuk.platformcore.domain.member.repository.MemberRepository;
import gdsc.konkuk.platformcore.util.fixture.member.MemberRegisterRequestFixture;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AttendanceIntegrationTest {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ParticipantRepository participantRepository;


    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
        attendanceRepository.deleteAll();
        participantRepository.deleteAll();
    }

    @Test
    @DisplayName("출석 등록 : 정상적인 출석 등록")
    void should_success_when_register_attendance() {
        // given
        String title = "example";
        String batch = "24-25";

        Member batchMember1 = memberService.register(MemberRegisterRequestFixture.builder()
                .batch(batch)
                .studentId("student1")
                .email("student1@gmail.com")
                .build()
                .getFixture()
                .toCommand());
        Member batchMember2 = memberService.register(MemberRegisterRequestFixture.builder()
                .batch(batch)
                .studentId("student2")
                .email("student2@gmail.com")
                .build()
                .getFixture()
                .toCommand());
        Member anotherBatchMember = memberService.register(MemberRegisterRequestFixture.builder()
                .batch("26-27")
                .studentId("student3")
                .email("student3@gmail.com")
                .build()
                .getFixture()
                .toCommand());

        // when
        Attendance savedAttendance = attendanceService.registerAttendance(title, batch);
        AttendanceStatus attendanceStatus = attendanceService.getAttendanceStatus(
                savedAttendance.getId());

        // then
        assertThat(savedAttendance.getTitle()).isEqualTo(title);
        assertThat(attendanceStatus.getTotal()).isEqualTo(2);
    }

    @Test
    @DisplayName("출석 조회 : 이달의 모든 출석 조회 성공")
    void should_success_when_get_all_month_attendance() {
        //given
        Attendance attendance1 = attendanceService.registerAttendance("example", "24-25");
        Attendance attendance2 = attendanceService.registerAttendance("example", "24-25");
        Attendance attendance3 = attendanceService.registerAttendance("example", "24-25");
        Attendance outOfPeriodAttendance = attendanceRepository.save(
                AttendanceFixture.builder()
                        .attendanceTime(LocalDateTime.now().minusMonths(1))
                        .build().getFixture());

        // when
        List<Attendance> attendanceList = attendanceService.getAllByPeriod(LocalDate.now());

        // then
        assertThat(attendanceList.size()).isEqualTo(3);
        assertThat(attendanceList.stream().map(Attendance::getId))
                .contains(attendance1.getId(), attendance2.getId(), attendance3.getId());
    }

    @Test
    @DisplayName("출석 : 정상적인 출석")
    void should_success_when_attend() {
        //given
        Member member = memberService.register(MemberRegisterRequestFixture.builder()
                .batch("24-25")
                .build()
                .getFixture()
                .toCommand());
        Attendance attendance = attendanceService.registerAttendance("example", "24-25");

        // when
        Participant participant = attendanceService.attend(
                member.getEmail(), attendance.getId(), attendance.getActiveQrUuid());

        // then
        assertThat(participant.getMemberId()).isEqualTo(member.getId());
        assertThat(participant.getAttendanceType()).isEqualTo(AttendanceType.ATTEND);
    }

    @Test
    @DisplayName("출석 : 출석이 종료된 경우 실패")
    void should_fail_when_attendance_end() {
        //given
        Member member = memberService.register(MemberRegisterRequestFixture.builder()
                .batch("24-25")
                .build()
                .getFixture()
                .toCommand());
        Attendance attendance = attendanceService.registerAttendance("example", "24-25");
        String qrUuid = attendance.getActiveQrUuid();

        // when
        attendanceService.expireQr(attendance.getId());

        // then
        assertThrows(QrInvalidException.class,
                () -> attendanceService.attend(member.getEmail(), attendance.getId(), qrUuid));
    }

    @Test
    @DisplayName("출석 : QR코드가 null이면 실패")
    void should_fail_when_no_Qr() {
        //given
        Member member = memberService.register(MemberRegisterRequestFixture.builder()
                .batch("24-25")
                .build()
                .getFixture()
                .toCommand());
        Attendance attendance = attendanceService.registerAttendance("example", "24-25");

        // when, then
        assertThrows(QrInvalidException.class,
                () -> attendanceService.attend(member.getEmail(), attendance.getId(), null));
    }

    @Test
    @DisplayName("출석 : QR코드가 불일치하면 실패")
    void should_fail_when_wrong_Qr() {
        //given
        Member member = memberService.register(MemberRegisterRequestFixture.builder()
                .batch("24-25")
                .build()
                .getFixture()
                .toCommand());
        Attendance attendance = attendanceService.registerAttendance("example", "24-25");

        // when, then
        assertThrows(QrInvalidException.class,
                () -> attendanceService.attend(member.getEmail(), attendance.getId(),
                        attendance.getActiveQrUuid() + "make it wrong"));
    }

    @Test
    @DisplayName("출석 : 존재하지 않는 멤버가 출석 시도 시 실패")
    void should_fail_when_member_not_exist() {
        //given
        Attendance attendance = attendanceService.registerAttendance("example", "24-25");

        // when, then
        assertThrows(UserNotFoundException.class,
                () -> attendanceService.attend("not exist", attendance.getId(),
                        attendance.getActiveQrUuid()));
    }

    @Test
    @DisplayName("출석 현황 조회 : 출석 현황 조회 성공")
    void should_success_when_get_attendance_status() {
        //given
        Member member1 = memberService.register(MemberRegisterRequestFixture.builder()
                .batch("24-25")
                .studentId("student1")
                .email("student1@gmail.com")
                .build()
                .getFixture()
                .toCommand());
        Member member2 = memberService.register(MemberRegisterRequestFixture.builder()
                .batch("24-25")
                .studentId("student2")
                .email("student2@email.com")
                .build()
                .getFixture()
                .toCommand());
        Member member3 = memberService.register(MemberRegisterRequestFixture.builder()
                .batch("24-25")
                .studentId("student3")
                .email("student3@email.com")
                .build()
                .getFixture()
                .toCommand());
        Attendance attendance = attendanceService.registerAttendance("example", "24-25");
        attendanceService.attend(member1.getEmail(), attendance.getId(),
                attendance.getActiveQrUuid());

        // when
        AttendanceStatus attendanceStatus = attendanceService.getAttendanceStatus(
                attendance.getId());

        // then
        assertThat(attendanceStatus.getAttendanceId()).isEqualTo(attendance.getId());
        assertThat(attendanceStatus.getTotal()).isEqualTo(3);
        assertThat(attendanceStatus.getAttended()).isEqualTo(1);
    }

    @Test
    @DisplayName("출석 삭제 : 삭제한 출석에는 참여자가 없어야 함")
    void should_success_when_delete_attendance() {
        //given
        Member member = memberService.register(MemberRegisterRequestFixture.builder()
                .batch("24-25")
                .build()
                .getFixture()
                .toCommand());
        Attendance attendance = attendanceService.registerAttendance("example", "24-25");

        assert attendanceRepository.findById(attendance.getId()).isPresent();
        assert participantRepository.findAllByAttendanceId(attendance.getId()).size() == 1;

        // when
        attendanceService.deleteAttendance(attendance.getId());

        // then
        assertThat(attendanceRepository.findById(attendance.getId())).isEmpty();
        assertThat(participantRepository.findAllByAttendanceId(attendance.getId()).size())
                .isEqualTo(0);
    }

    @Test
    @DisplayName("출석 만료 : 만료된 출석에는 참여자가 유지되어야 함")
    void should_success_when_expire_attendance() {
        //given
        Member member = memberService.register(MemberRegisterRequestFixture.builder()
                .batch("24-25")
                .build()
                .getFixture()
                .toCommand());
        Attendance attendance = attendanceService.registerAttendance("example", "24-25");

        assert attendanceRepository.findById(attendance.getId()).isPresent();
        assert participantRepository.findAllByAttendanceId(attendance.getId()).size() == 1;

        // when
        attendanceService.expireQr(attendance.getId());

        // then
        assertThat(attendanceRepository.findById(attendance.getId())).isPresent();
        assertThat(participantRepository.findAllByAttendanceId(attendance.getId()).size())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("출석 재시작 : 만료된 출석 재시작 성공")
    void should_success_when_restart_attendance() {
        //given
        Member member = memberService.register(MemberRegisterRequestFixture.builder()
                .batch("24-25")
                .build()
                .getFixture()
                .toCommand());
        Attendance attendance = attendanceService.registerAttendance("example", "24-25");
        attendanceService.expireQr(attendance.getId());

        // when
        Attendance restartedAttendance = attendanceService.generateQr(attendance.getId());
        Participant participant = attendanceService.attend(
                member.getEmail(), restartedAttendance.getId(),
                restartedAttendance.getActiveQrUuid());

        // then
        assertThat(restartedAttendance.getActiveQrUuid()).isNotNull();
        assertThat(participant.getAttendanceType()).isEqualTo(AttendanceType.ATTEND);
    }
}
