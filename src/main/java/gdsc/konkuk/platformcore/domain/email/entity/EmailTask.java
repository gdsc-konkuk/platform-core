package gdsc.konkuk.platformcore.domain.email.entity;

import static gdsc.konkuk.platformcore.global.utils.FieldValidator.validateNotNull;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    @OneToMany(mappedBy = "emailTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmailReceiver> receivers = new ArrayList<>();

    @Column(name = "send_at")
    private LocalDateTime sendAt;

    @Column(name = "is_sent")
    private boolean isSent = false;

    @Builder
    public EmailTask(
            Long id, EmailDetail emailDetail, List<EmailReceiver> emailReceivers, LocalDateTime sendAt) {
        this.id = id;
        this.emailDetail = validateNotNull(emailDetail, "emailDetails");
        this.receivers = new ArrayList<>();
        if (emailReceivers != null) {
            emailReceivers.forEach(this::addReceiver);
        }
        this.sendAt = validateNotNull(sendAt, "sendAt");
    }

    public void changeEmailDetails(final EmailDetail newEmailDetail) {
        emailDetail = newEmailDetail;
    }

    public void changeEmailReceivers(Set<EmailReceiver> newReceivers) {
        this.receivers.clear();
        newReceivers.forEach(this::addReceiver);
    }

    public void changeSendAt(final LocalDateTime newSendAt) {
        sendAt = newSendAt;
    }

    public void markAsSent() {
        isSent = true;
    }

    public List<EmailReceiver> getWaitingReceivers() {
        return receivers.stream()
            .filter(r -> r.getSendStatus() == EmailSendStatus.WAITING)
            .toList();
    }

    public boolean isAllCompleted() {
        return receivers.stream()
            .allMatch(r -> r.getSendStatus() == EmailSendStatus.COMPLETED);
    }

    public long getCompletedCount() {
        return receivers.stream()
            .filter(r -> r.getSendStatus() == EmailSendStatus.COMPLETED)
            .count();
    }

    public Long getLastWaitingPeriodInSeconds() {
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), this.sendAt);
    }

    private void addReceiver(EmailReceiver receiver) {
        receivers.add(receiver);
        receiver.setEmailTask(this); // 양방향 연관관계 설정
    }

}
