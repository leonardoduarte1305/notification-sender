package br.dev.notificationsender.service;

import br.dev.notificationsender.events.contratos.EmailDTO;
import br.dev.notificationsender.exceptions.EmailSendingFailureException;
import br.dev.notificationsender.service.validations.EmailDTOValidator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${app.mail.from}")
    private String from;

    private final JavaMailSender mailSender;

    private final EmailDTOValidator validator;

    private final MimeMessageFactory mimeMessageFactory;

    @Retryable(retryFor = EmailSendingFailureException.class, maxAttempts = 2, backoff = @Backoff(delay = 3000))
    public void enviarEmail(EmailDTO emailDTO) {
        validator.validate(emailDTO);

        try {
            MimeMessage emailPreenchido = mimeMessageFactory.preencher(from, emailDTO, mailSender);

            mailSender.send(emailPreenchido);
            log.info("E-mail enviado com sucesso. Quantidade de destinatários {}", emailDTO.getTo().size());
        } catch (MessagingException | MailException e) {
            log.error("Erro ao enviar e-mail. Quantidade de destinatários {}", emailDTO.getTo().size(), e);

            throw new EmailSendingFailureException(e.getLocalizedMessage(), e);
        }
    }

}
