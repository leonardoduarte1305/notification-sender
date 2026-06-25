package br.dev.notificationsender.events.validation;

import br.dev.notificationsender.events.contratos.FaturaEmitidaEvent;
import br.dev.notificationsender.exceptions.InvalidEmailEventPayloadException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static br.dev.notificationsender.events.contratos.enumx.EventType.FATURA_EMITIDA;

@Component
public class FaturaEmitidaEventValidator {

    public void validate(FaturaEmitidaEvent payload) {
        List<String> errors = new ArrayList<>();

        if (payload == null) {
            throw new InvalidEmailEventPayloadException("Payload do evento de fatura emitida nao pode ser nulo.");
        }

        validateRequiredFields(payload, errors);
        validatePositiveNumbers(payload, errors);
        validateRecipient(payload.destinatario(), errors);

        if (!errors.isEmpty()) {
            throw new InvalidEmailEventPayloadException("Payload do evento de fatura emitida invalido: " + String.join("; ", errors));
        }
    }

    private static void validateRequiredFields(FaturaEmitidaEvent payload, List<String> errors) {
        if (payload.eventId() == null) {
            errors.add("eventId é obrigatório");
        }

        if (payload.eventType() == null) {
            errors.add("eventType é obrigatório");
        } else if (!FATURA_EMITIDA.equals(payload.eventType())) {
            errors.add("eventType deve ser FATURA_EMITIDA");
        }

        if (payload.numeroApartamento() == null) {
            errors.add("numeroApartamento é obrigatório");
        }

        if (payload.valorTotal() == null) {
            errors.add("valorTotal é obrigatório");
        }

        if (payload.dataVencimento() == null) {
            errors.add("dataVencimento é obrigatório");
        }

        if (payload.faturaId() == null) {
            errors.add("faturaId é obrigatório");
        }
    }

    private static void validatePositiveNumbers(FaturaEmitidaEvent payload, List<String> errors) {
        if (payload.numeroApartamento() != null && payload.numeroApartamento() <= 0) {
            errors.add("numeroApartamento deve ser positivo");
        }

        if (payload.valorTotal() != null && payload.valorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("valorTotal deve ser maior que zero");
        }

        if (payload.faturaId() != null && payload.faturaId() <= 0) {
            errors.add("faturaId deve ser positivo");
        }
    }

    private static void validateRecipient(String destinatario, List<String> errors) {
        if (destinatario == null || destinatario.isBlank()) {
            errors.add("destinatário é obrigatório");
            return;
        }

        try {
            InternetAddress internetAddress = new InternetAddress(destinatario);
            internetAddress.validate();
        } catch (AddressException e) {
            errors.add("destinatário deve ser um e-mail valido");
        }
    }

}
