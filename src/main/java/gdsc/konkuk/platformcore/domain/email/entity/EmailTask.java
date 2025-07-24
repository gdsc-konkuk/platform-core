package gdsc.konkuk.platformcore.domain.email.entity;

import static gdsc.konkuk.platformcore.global.utils.FieldValidator.validateNotNull;

import gdsc.konkuk.platformcore.global.utils.BooleanToIntegerConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @Embedded
    private EmailDetail emailDetail;

    @Column(name = "send_at")
    private LocalDateTime sendAt;

    @Column(name = "is_sent")
    @Convert(converter = BooleanToIntegerConverter.class)
    private Boolean isSent = false;

    @Builder
    public EmailTask(
            Long id, EmailDetail emailDetail, LocalDateTime sendAt) {
        this.id = id;
        this.emailDetail = validateNotNull(emailDetail, "emailDetails");
        this.sendAt = validateNotNull(sendAt, "sendAt");
    }

    public void changeEmailDetails(final EmailDetail newEmailDetail) {
        emailDetail = newEmailDetail;
    }

    public void changeSendAt(final LocalDateTime newSendAt) {
        sendAt = newSendAt;
    }

    public void markAsSent() {
        isSent = true;
    }

    public Long getLastWaitingPeriodInSeconds() {
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), this.sendAt);
    }

    public Boolean isSent() {
        return isSent;
    }
}
