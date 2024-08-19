package gdsc.konkuk.platformcore.external.email;

import gdsc.konkuk.platformcore.domain.email.entity.EmailReceiver;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailParseException;

import org.springframework.stereotype.Component;

import gdsc.konkuk.platformcore.domain.email.entity.EmailDetails;
import gdsc.konkuk.platformcore.domain.email.entity.EmailTask;
import gdsc.konkuk.platformcore.external.email.exceptions.EmailClientErrorCode;
import gdsc.konkuk.platformcore.external.email.exceptions.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailClient {

  private final JavaMailSender javaMailSender;

  public void sendEmailToReceivers(EmailTask email) {
    EmailDetails emailDetails = email.getEmailDetails();
    Set<EmailReceiver> receivers = email.getEmailReceivers().getReceivers();
    receivers.forEach(receiver -> sendEmail(receiver, emailDetails));
  }

  private void sendEmail(EmailReceiver to, EmailDetails emailDetails) {
    try {
      log.info("Sending email to {}", to);
      String emailContent = emailDetails.getContent().replaceAll("\\{이름}", to.getName());
      MimeMessage message =
          convertToHTMLMimeMessage(to.getEmail(), emailDetails.getSubject(), emailContent);
      javaMailSender.send(message);
    } catch (MailParseException | MessagingException e) {
      throw EmailSendingException.of(EmailClientErrorCode.MAIL_PARSING_ERROR, e.getMessage());
    } catch (MailException e) {
      throw EmailSendingException.of(EmailClientErrorCode.MAIL_SENDING_ERROR, e.getMessage());
    }
  }

  private MimeMessage convertToHTMLMimeMessage(String to, String subject, String text)
      throws MessagingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    message.setSubject(subject);
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(to);
    helper.setText(text, true);
    return message;
  }
}
