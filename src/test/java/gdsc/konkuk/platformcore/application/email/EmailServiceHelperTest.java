package gdsc.konkuk.platformcore.application.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

import gdsc.konkuk.platformcore.application.email.exceptions.EmailNotFoundException;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.domain.email.repository.EmailTaskRepository;
import gdsc.konkuk.platformcore.fixture.email.EmailTaskFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;

class EmailServiceHelperTest {

    @Mock
    private EmailTaskRepository emailTaskRepository;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void should_return_task_when_exists() {
        // given
        EmailTask emailTaskToRequest = EmailTaskFixture.builder().build().getFixture();
        given(emailTaskRepository.findById(emailTaskToRequest.getId()))
                .willReturn(Optional.of(emailTaskToRequest));

        // when
        EmailTask emailTaskFound =
                EmailServiceHelper.findEmailTaskById(emailTaskRepository,
                        emailTaskToRequest.getId());

        // then
        assertEquals(emailTaskToRequest.getEmailDetail(), emailTaskFound.getEmailDetail());
        assertEquals(emailTaskToRequest.getEmailReceivers(), emailTaskFound.getEmailReceivers());
    }

    @Test
    void should_fail_when_not_exists() {
        // given
        given(emailTaskRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when
        Executable executable =
                () -> EmailServiceHelper.findEmailTaskById(emailTaskRepository, 0L);

        // then
        assertThrows(EmailNotFoundException.class, executable);
    }
}
