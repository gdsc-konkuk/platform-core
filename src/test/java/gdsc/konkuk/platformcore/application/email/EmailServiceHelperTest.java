package gdsc.konkuk.platformcore.application.email;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.MockitoAnnotations.*;

import gdsc.konkuk.platformcore.application.email.exceptions.EmailNotFoundException;
import gdsc.konkuk.platformcore.domain.email.entity.EmailDetails;
import gdsc.konkuk.platformcore.domain.email.entity.EmailReceivers;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.domain.email.repository.EmailTaskRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;

class EmailServiceHelperTest {

  @Mock private EmailTaskRepository emailTaskRepository;

  private final EmailTask mock1 =
      EmailTask.builder()
          .id(1L)
          .emailDetails(new EmailDetails("subject", "content"))
          .receivers(new EmailReceivers(Set.of("example1.com", "example2.com")))
          .sendAt(LocalDateTime.of(2021, 1, 1, 1, 1))
          .build();

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void should_return_task_when_exists() {
    // given
    given(emailTaskRepository.findById(any())).willReturn(Optional.of(mock1));

    // when
    EmailTask emailTask = EmailServiceHelper.findEmailTaskById(emailTaskRepository, 1L);

    // then
    assertEquals(mock1.getEmailDetails(), emailTask.getEmailDetails());
    assertEquals(mock1.getEmailReceivers(), emailTask.getEmailReceivers());
  }

  @Test
  void should_fail_when_not_exists() {
    // given
    given(emailTaskRepository.findById(any())).willReturn(Optional.empty());

    // when
    Executable executable = () -> EmailServiceHelper.findEmailTaskById(emailTaskRepository, 1L);

    // then
    assertThrows(EmailNotFoundException.class, executable);
  }
}
