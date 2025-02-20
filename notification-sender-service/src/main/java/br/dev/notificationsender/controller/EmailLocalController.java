package br.dev.notificationsender.controller;

import br.dev.leoduarte.notificationsender.server.EnviarEmailApi;
import br.dev.leoduarte.notificationsender.server.model.EmailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Profile("local")
@RestController
@RequiredArgsConstructor
public class EmailLocalController implements EnviarEmailApi {

    @Override
    public ResponseEntity<Void> enviarEmailSemAnexo(EmailDTO emailDTO) {
        log.error("Email enviado localmente de \"{}\" para os destinatários {}, com o assunto: \"{}\" e com a mensagem: \"{}\"",
                emailDTO.getFrom(),
                emailDTO.getTo(),
                emailDTO.getSubject(),
                emailDTO.getMessage());
        return ResponseEntity.ok().build();
    }

}
