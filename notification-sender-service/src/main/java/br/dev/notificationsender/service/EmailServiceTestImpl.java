package br.dev.notificationsender.service;

import br.dev.leoduarte.notificationsender.server.model.EmailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Profile("test")
@RequiredArgsConstructor
public class EmailServiceTestImpl implements EmailService {

    @Override
    public CompletableFuture<Void> enviarEmail(EmailDTO emailDTO) {
        log.info("Enviando email de: {}, para: {}, assunto: {}.", emailDTO.getFrom(), emailDTO.getTo(), emailDTO.getSubject());
        return CompletableFuture.completedFuture(null);
    }

}
