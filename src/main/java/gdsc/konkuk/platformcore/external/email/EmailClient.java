package gdsc.konkuk.platformcore.external.email;

import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailParseException;

import org.springframework.stereotype.Component;

import gdsc.konkuk.platformcore.domain.email.entity.PlatformEmail;
import gdsc.konkuk.platformcore.domain.email.entity.Receiver;
import gdsc.konkuk.platformcore.external.email.exceptions.EmailErrorCode;
import gdsc.konkuk.platformcore.external.email.exceptions.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailClient {

  private final JavaMailSender javaMailSender;

  public void sendEmailToReceivers(PlatformEmail email) {
    List<Receiver> receivers = email.getReceivers();
    receivers.forEach(receiver -> sendEmail(receiver.getDest(), email.getSubject(), email.getContent()));
  }

  private void sendEmail(String to, String subject, String text) {
    try {
      MimeMessage message = convertToHTMLMimeMessage(to, subject, text);
      javaMailSender.send(message);
    } catch (MailParseException e) {
      throw EmailSendingException.of(EmailErrorCode.MAIL_PARSING_ERROR, e.getMessage());
    } catch (MessagingException e) {
      throw EmailSendingException.of(EmailErrorCode.MAIL_SENDING_ERROR, e.getMessage());
    }
  }

  private MimeMessage convertToHTMLMimeMessage(String to, String subject, String text) throws MessagingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    message.setSubject(subject);
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(to);
    helper.setText(text, true);
    return message;
  }
}
