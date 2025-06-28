package gdsc.konkuk.platformcore.domain.email.entity;

import lombok.Getter;

/**
 * 이메일 전송 상태를 나타내는 Enum
 * State Transition: WAITING -> PENDING -> COMPLETED
 *                            -> FAILED (timeout or error)
 */
@Getter
public enum EmailSendStatus {

    WAITING("대기중"),
    PENDING("전송중"),
    COMPLETED("전송완료"),
    FAILED("전송실패");
    
    private final String description;
    
    EmailSendStatus(String description) {
        this.description = description;
    }

    public boolean canTransitionTo(EmailSendStatus newStatus) {
        return switch (this) {
            case WAITING -> newStatus == PENDING;
            case PENDING -> newStatus == COMPLETED || newStatus == FAILED;
            case COMPLETED, FAILED -> false;
            default -> false;
        };
    }

    public boolean isRetryable() {
        return this == WAITING || this == FAILED;
    }

    public boolean isFinalStatus() {
        return this == COMPLETED || this == FAILED;
    }
}
