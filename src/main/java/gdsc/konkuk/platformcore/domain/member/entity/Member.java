package gdsc.konkuk.platformcore.domain.member.entity;

import static gdsc.konkuk.platformcore.global.consts.PlatformConstants.SOFT_DELETE_RETENTION_MONTHS;
import static gdsc.konkuk.platformcore.global.utils.FieldValidator.validateNotNull;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;

import gdsc.konkuk.platformcore.application.member.exceptions.MemberErrorCode;
import gdsc.konkuk.platformcore.application.member.exceptions.UserAlreadyDeletedException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@SQLRestriction("is_deleted = false")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id", unique = true)
	private String memberId;

	@Column(name = "password")
	private String password;

	@Column(name = "member_name")
	private String name;

	@Column(name = "member_email")
	private String email;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Column(name = "is_activated")
	private boolean isActivated = true;

	@Column(name = "is_deleted")
	private boolean isDeleted = false;

	@Column(name = "soft_deleted_at")
	private LocalDateTime softDeletedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "member_role")
	private MemberRole role = MemberRole.MEMBER;

	@Column(name = "batch")
	private int batch;

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

	public Boolean isMemberActivated() {
		return isActivated;
	}

	@Builder
	public Member(Long id, String memberId, String password, String name, String email, String profileImageUrl,
		int batch) {
		this.id = id;
		this.memberId = validateNotNull(memberId, "memberId");
		this.password = validateNotNull(password, "password");
		this.name = validateNotNull(name, "name");
		this.email = validateNotNull(email, "email");
		this.profileImageUrl = profileImageUrl;
		this.batch = batch;
	}
}
