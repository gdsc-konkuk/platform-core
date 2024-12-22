package gdsc.konkuk.platformcore.application.email;

import gdsc.konkuk.platformcore.application.email.exceptions.EmailErrorCode;
import gdsc.konkuk.platformcore.application.email.exceptions.EmailNotFoundException;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.domain.email.repository.EmailTaskRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmailServiceHelper {

    public static EmailTask findEmailTaskById(EmailTaskRepository repository, Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> EmailNotFoundException.of(EmailErrorCode.EMAIL_NOT_FOUND));
    }
}
