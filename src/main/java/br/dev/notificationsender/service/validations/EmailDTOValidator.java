package br.dev.notificationsender.service.validations;

import br.dev.notificationsender.events.contratos.EmailDTO;
import br.dev.notificationsender.exceptions.InvalidEmailDTOException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static br.dev.notificationsender.commons.ErrorMessages.CAMPO_NAO_PODE_SER_NULO;
import static br.dev.notificationsender.commons.ErrorMessages.CAMPO_NAO_PODE_SER_NULO_OU_VAZIO;
import static br.dev.notificationsender.commons.ErrorMessages.CAMPO_OBRIGATORIO;
import static br.dev.notificationsender.commons.ErrorMessages.DEVE_SER_EMAIL_VALIDO;

@Component
@RequiredArgsConstructor
public class EmailDTOValidator {

    public void validate(EmailDTO payload) {
        List<String> errors = new ArrayList<>();

        if (payload == null) {
            throw new InvalidEmailDTOException(CAMPO_NAO_PODE_SER_NULO.format("EmailDTO"));
        }

        if (payload.getSubject() == null || payload.getSubject().isEmpty() || payload.getSubject().isBlank()) {
            errors.add(CAMPO_NAO_PODE_SER_NULO_OU_VAZIO.format("subject"));
        }

        if (payload.getMessage() == null || payload.getMessage().isEmpty() || payload.getMessage().isBlank()) {
            errors.add(CAMPO_NAO_PODE_SER_NULO_OU_VAZIO.format("message"));
        }

        validaTo(payload.getTo(), errors);

        if (!errors.isEmpty()) {
            throw new InvalidEmailDTOException("EmailDTO invalido: " + String.join("; ", errors));
        }

    }

    private void validaTo(List<String> destinatarios, List<String> errors) {
        if (destinatarios == null || destinatarios.isEmpty()) {
            errors.add(CAMPO_OBRIGATORIO.format("to"));
            return;
        }

        destinatarios.forEach(destinatario -> {
            if (destinatario == null || destinatario.isBlank()) {
                errors.add(CAMPO_NAO_PODE_SER_NULO_OU_VAZIO.format("destinatário"));
                return;
            }

            try {
                InternetAddress internetAddress = new InternetAddress(destinatario);
                internetAddress.validate();
            } catch (AddressException e) {
                errors.add(DEVE_SER_EMAIL_VALIDO.format(destinatario));
            }
        });
    }


}
