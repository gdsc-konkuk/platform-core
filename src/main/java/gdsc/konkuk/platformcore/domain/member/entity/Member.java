package gdsc.konkuk.platformcore.domain.member.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	@Id @GeneratedValue
	private Long id;

	@Column(name = "member_id",unique = true)
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
	private boolean isActivated;

	@Column(name = "is_deleted")
	private DeleteStatus isDeleted;

	@Column(name = "soft_deleted_at")
	private LocalDateTime softDeletedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "member_role")
	private MemberRole role;

	@Column(name = "batch")
	private int batch;

	@Builder
	public Member(Long id, String memberId, String password, String name, String email, String profileImageUrl,
		boolean isActivated, DeleteStatus isDeleted, LocalDateTime deletedAt, MemberRole role, int batch) {
		this.id = id;
		this.memberId = memberId;
		this.password = password;
		this.name = name;
		this.email = email;
		this.profileImageUrl = profileImageUrl;
		this.isActivated = isActivated;
		this.isDeleted = isDeleted;
		this.softDeletedAt = deletedAt;
		this.role = role;
		this.batch = batch;
	}
}
