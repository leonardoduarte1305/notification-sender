package br.dev.notificationsender.service;

import br.dev.leoduarte.notificationsender.server.model.EmailDTO;
import br.dev.notificationsender.configuration.email.EmailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Profile("prod")
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final EmailConfig emailConfig;

    @Async
    @Retryable(retryFor = MessagingException.class, maxAttempts = 2, backoff = @Backoff(delay = 3000))
    public CompletableFuture<Void> enviarEmail(EmailDTO emailDTO) {
        try {
            MimeMessage emailPreenchido = new PreencherEmail().preencher(emailConfig.getFrom(), emailDTO, mailSender);

            log.debug("Enviando email de: {}, para: {}, assunto: {}.", emailDTO.getFrom(), emailDTO.getTo(), emailDTO.getSubject());

            mailSender.send(emailPreenchido);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Erro ao enviar e-mail de: {} para: {}. Assunto: {}.",
                    emailDTO.getFrom(), emailDTO.getTo(), emailDTO.getSubject(), e);

            return CompletableFuture.failedFuture(e);
        }
    }

}
