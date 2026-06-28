package br.dev.notificationsender.events.validation;

import br.dev.notificationsender.events.contratos.FaturaEmitidaEvent;
import br.dev.notificationsender.exceptions.InvalidEmailEventPayloadException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static br.dev.notificationsender.commons.ErrorMessages.CAMPO_OBRIGATORIO;
import static br.dev.notificationsender.commons.ErrorMessages.DEVE_SER_EMAIL_VALIDO;
import static br.dev.notificationsender.commons.ErrorMessages.DEVE_SER_MAIOR_QUE_ZERO;
import static br.dev.notificationsender.commons.ErrorMessages.DEVE_SER_POSITIVO;
import static br.dev.notificationsender.commons.ErrorMessages.PAYLOAD_FATURA_EMITIDA_INVALIDO;
import static br.dev.notificationsender.commons.ErrorMessages.PAYLOAD_NAO_PODE_SER_NULO;
import static br.dev.notificationsender.events.contratos.enumx.EventType.FATURA_EMITIDA;
import static java.lang.String.join;

@Component
public class FaturaEmitidaEventValidator {

    public void validate(FaturaEmitidaEvent payload) {
        List<String> errors = new ArrayList<>();

        if (payload == null) {
            throw new InvalidEmailEventPayloadException(PAYLOAD_NAO_PODE_SER_NULO.getMessage());
        }

        validateRequiredFields(payload, errors);
        validatePositiveNumbers(payload, errors);
        validateRecipient(payload.destinatario(), errors);

        if (!errors.isEmpty()) {
            throw new InvalidEmailEventPayloadException(PAYLOAD_FATURA_EMITIDA_INVALIDO.format(join("; ", errors)));
        }
    }

    private static void validateRequiredFields(FaturaEmitidaEvent payload, List<String> errors) {
        if (payload.eventId() == null) {
            errors.add(CAMPO_OBRIGATORIO.format("eventId"));
        }

        if (payload.eventType() == null) {
            errors.add(CAMPO_OBRIGATORIO.format("eventType"));
        } else if (!FATURA_EMITIDA.equals(payload.eventType())) {
            errors.add("eventType deve ser FATURA_EMITIDA");
        }

        if (payload.numeroApartamento() == null) {
            errors.add(CAMPO_OBRIGATORIO.format("numeroApartamento"));
        }

        if (payload.valorTotal() == null) {
            errors.add(CAMPO_OBRIGATORIO.format("valorTotal"));
        }

        if (payload.dataVencimento() == null) {
            errors.add(CAMPO_OBRIGATORIO.format("dataVencimento"));
        }

        if (payload.faturaId() == null) {
            errors.add(CAMPO_OBRIGATORIO.format("faturaId"));
        }
    }

    private static void validatePositiveNumbers(FaturaEmitidaEvent payload, List<String> errors) {
        if (payload.numeroApartamento() != null && payload.numeroApartamento() <= 0) {
            errors.add(DEVE_SER_POSITIVO.format("numeroApartamento"));
        }

        if (payload.valorTotal() != null && payload.valorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(DEVE_SER_MAIOR_QUE_ZERO.format("valorTotal"));
        }

        if (payload.faturaId() != null && payload.faturaId() <= 0) {
            errors.add(DEVE_SER_POSITIVO.format("faturaId"));
        }
    }

    private static void validateRecipient(String destinatario, List<String> errors) {
        if (destinatario == null || destinatario.isBlank()) {
            errors.add(CAMPO_OBRIGATORIO.format("destinatário"));
            return;
        }

        try {
            InternetAddress internetAddress = new InternetAddress(destinatario);
            internetAddress.validate();
        } catch (AddressException e) {
            errors.add(DEVE_SER_EMAIL_VALIDO.format("destinatário"));
        }
    }

}
