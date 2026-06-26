package br.dev.notificationsender.service;

import br.dev.notificationsender.events.contratos.EmailDTO;
import br.dev.notificationsender.exceptions.EmailSendingFailureExeption;
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

    private final MimeMessageFactory mimeMessageFactory;

    @Retryable(retryFor = EmailSendingFailureExeption.class, maxAttempts = 2, backoff = @Backoff(delay = 3000))
    public void enviarEmail(EmailDTO emailDTO) {
        try {
            MimeMessage emailPreenchido = mimeMessageFactory.preencher(from, emailDTO, mailSender);

            log.debug("Enviando email de: {}, para: {}, assunto: {}.", from, emailDTO.getTo(), emailDTO.getSubject());

            mailSender.send(emailPreenchido);
        } catch (MessagingException | MailException e) {
            log.error("Erro ao enviar e-mail de: {} para: {}. Assunto: {}.",
                    from, emailDTO.getTo(), emailDTO.getSubject(), e);

            throw new EmailSendingFailureExeption(e.getLocalizedMessage(), e);
        }
    }

}
