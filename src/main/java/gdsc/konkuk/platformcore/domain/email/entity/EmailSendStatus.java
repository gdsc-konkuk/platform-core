package gdsc.konkuk.platformcore.domain.email.entity;

/**
 * 이메일 전송 상태를 나타내는 Enum
 * State Transition: WAITING -> PENDING -> COMPLETED
 *                            -> FAILED (timeout or error)
 */
public enum EmailSendStatus {

    WAITING("대기중"),
    PENDING("전송중"),
    COMPLETED("전송완료"),
    FAILED("전송실패");
    
    private final String description;
    
    EmailSendStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

    public boolean canTransitionTo(EmailSendStatus newStatus) {
        switch (this) {
            case WAITING:
                return newStatus == PENDING;
            case PENDING:
                return newStatus == COMPLETED || newStatus == FAILED;
            case COMPLETED:
            case FAILED:
                return false;
            default:
                return false;
        }
    }


    public boolean isRetryable() {
        return this == WAITING || this == FAILED;
    }

    public boolean isFinalStatus() {
        return this == COMPLETED || this == FAILED;
    }
}
