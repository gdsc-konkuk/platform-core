package gdsc.konkuk.platformcore.application.email;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gdsc.konkuk.platformcore.controller.email.dto.EmailSendRequest;
import gdsc.konkuk.platformcore.domain.email.entity.Email;
import gdsc.konkuk.platformcore.domain.email.repository.EmailRepository;
import gdsc.konkuk.platformcore.external.email.EmailClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final EmailClient emailClient;
  private final EmailRepository emailRepository;

  @Transactional
  public Long process(EmailSendRequest request) {
    Email email = Email.builder()
      .subject(request.getSubject())
      .content(request.getContent())
      .build();
    email.addReceivers(request.getReceivers());
    emailRepository.save(email);
    emailClient.sendAll(email);
    return email.getId();
  }
}
