package gdsc.konkuk.platformcore.domain.member.entity;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.SOFT_DELETE_RETENTION_MONTHS;
import static gdsc.konkuk.platformcore.global.utils.FieldValidator.validateNotNull;

import gdsc.konkuk.platformcore.application.member.dtos.MemberUpdateCommand;
import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyDeletedException;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import gdsc.konkuk.platformcore.global.utils.BooleanToIntegerConverter;

@Entity
@SQLRestriction("is_deleted = false")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", unique = true)
    private String studentId;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_email")
    private String email;

    @Column(name = "department")
    private String department;

    @Column(name = "is_deleted")
    @Convert(converter = BooleanToIntegerConverter.class)
    private Boolean isDeleted = false;

    @Column(name = "is_activated")
    @Convert(converter = BooleanToIntegerConverter.class)
    private Boolean isActivated = true;

    @Column(name = "soft_deleted_at")
    private LocalDateTime softDeletedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private MemberRole role;

    @Column(name = "batch")
    private String batch;

    @Builder
    public Member(
            Long id,
            String studentId,
            String name,
            String email,
            String department,
            String role,
            String batch) {
        this.id = id;
        this.studentId = validateNotNull(studentId, "studentId");
        this.name = validateNotNull(name, "name");
        this.email = validateNotNull(email, "email");
        this.department = validateNotNull(department, "department");
        this.role = (role != null) ? MemberRole.from(role) : MemberRole.MEMBER;
        this.batch = validateNotNull(batch, "batch");
    }

    public void withdraw() {
        if (isDeleted) {
            throw UserAlreadyDeletedException.of(MemberErrorCode.USER_ALREADY_DELETED);
        }
        isDeleted = true;
        softDeletedAt = LocalDateTime.now().plusMonths(SOFT_DELETE_RETENTION_MONTHS);
    }

    public Boolean isMemberDeleted() {
        return isDeleted;
    }

    public void update(MemberUpdateCommand command) {
        if (command.getStudentId() != null) {
            studentId = command.getStudentId();
        }
        if (command.getName() != null) {
            name = command.getName();
        }
        if (command.getEmail() != null) {
            email = command.getEmail();
        }
        if (command.getDepartment() != null) {
            department = command.getDepartment();
        }
        if (command.getRole() != null) {
            role = MemberRole.from(command.getRole());
        }
        if (command.getBatch() != null) {
            batch = command.getBatch();
        }
    }
}
