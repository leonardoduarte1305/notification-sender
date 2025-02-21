package br.dev.notificationsender.service;

import br.dev.leoduarte.notificationsender.server.model.EmailDTO;
import br.dev.notificationsender.configuration.email.EmailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("prod")
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final EmailConfig emailConfig;

    @Async
    public void enviarEmail(EmailDTO emailDTO) {
        emailDTO.getTo()
                .forEach(to -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(emailConfig.getFrom());
                helper.setTo(to);
                helper.setSubject(emailDTO.getSubject());
                helper.setText(emailDTO.getMessage());

                log.info("Enviando email de: {}, para: {}, com assunto: {} e corpo: {}",emailDTO.getFrom(), to, emailDTO.getSubject(), emailDTO.getMessage());

                mailSender.send(message);
            } catch ( MessagingException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

}
