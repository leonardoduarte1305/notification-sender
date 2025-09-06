package br.dev.notificationsender.controller;

import br.dev.leoduarte.notificationsender.server.EnviarEmailApi;
import br.dev.leoduarte.notificationsender.server.model.EmailDTO;
import br.dev.notificationsender.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailController implements EnviarEmailApi {

    private final EmailService emailService;

    @Override
    public ResponseEntity<Void> enviarEmailSemAnexo(EmailDTO emailDTO) {
        emailService.enviarEmail(emailDTO);
        return ResponseEntity.ok().build();
    }

}
