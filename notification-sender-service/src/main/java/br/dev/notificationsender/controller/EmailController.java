package br.dev.notificationsender.controller;

import br.dev.leoduarte.notificationsender.server.EnviarEmailApi;
import br.dev.leoduarte.notificationsender.server.model.EmailDTO;
import br.dev.notificationsender.service.EmailService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Hidden
@RestController
@RequiredArgsConstructor
public class EmailController implements EnviarEmailApi {

    private final EmailService emailService;

    @Override
    public ResponseEntity<Void> enviarEmailSemAnexo(String xApiKey, EmailDTO emailDTO) {
        emailService.enviarEmail(emailDTO);
        return ResponseEntity.ok().build();
    }

}
