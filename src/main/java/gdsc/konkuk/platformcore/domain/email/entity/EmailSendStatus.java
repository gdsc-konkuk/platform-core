package gdsc.konkuk.platformcore.domain.email.entity;

public enum EmailSendStatus {
    WAITING,
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED;

    public boolean canTransitionTo(EmailSendStatus newStatus) {
        return switch (this) {
            case WAITING -> newStatus == PENDING;
            case PENDING -> newStatus == COMPLETED || newStatus == FAILED || newStatus == CANCELLED;
            case COMPLETED, FAILED, CANCELLED -> false;
        };
    }
}
