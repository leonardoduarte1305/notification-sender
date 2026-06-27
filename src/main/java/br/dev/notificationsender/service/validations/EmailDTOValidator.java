package br.dev.notificationsender.service.validations;

import br.dev.notificationsender.events.contratos.EmailDTO;
import br.dev.notificationsender.exceptions.InvalidEmailDTOException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailDTOValidator {

    public void validate(EmailDTO payload) {
        List<String> errors = new ArrayList<>();

        if (payload == null) {
            throw new InvalidEmailDTOException("EmailDTO não pode ser nulo.");
        }

        if (payload.getSubject() == null || payload.getSubject().isEmpty() || payload.getSubject().isBlank()) {
            errors.add("\"subject\" não pode ser nulo ou estar vazio");
        }

        if (payload.getMessage() == null || payload.getMessage().isEmpty() || payload.getMessage().isBlank()) {
            errors.add("\"message\" não pode ser nula ou estar vazia");
        }

        validaTo(payload.getTo(), errors);

        if (!errors.isEmpty()) {
            throw new InvalidEmailDTOException("EmailDTO invalido: " + String.join("; ", errors));
        }

    }

    private void validaTo(List<String> destinatarios, List<String> errors) {
        if (destinatarios == null || destinatarios.isEmpty()) {
            errors.add("\"to\" é obrigatório");
            return;
        }

        destinatarios.forEach(destinatario -> {
            if (destinatario == null || destinatario.isBlank()) {
                errors.add("destinatário não pode ser nulo ou estar vazio");
                return;
            }

            try {
                InternetAddress internetAddress = new InternetAddress(destinatario);
                internetAddress.validate();
            } catch (AddressException e) {
                errors.add("destinatário encontrado com formato invalido: " + destinatario);
            }
        });
    }


}
