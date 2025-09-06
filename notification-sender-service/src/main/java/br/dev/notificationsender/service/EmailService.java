package br.dev.notificationsender.service;

import br.dev.leoduarte.notificationsender.server.model.EmailDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Profile("prod")
@Service
public interface EmailService {

    CompletableFuture<Void> enviarEmail(EmailDTO emailDTO);

}
