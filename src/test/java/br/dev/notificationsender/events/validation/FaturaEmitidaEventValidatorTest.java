package br.dev.notificationsender.events.validation;

import br.dev.notificationsender.events.contratos.FaturaEmitidaEvent;
import br.dev.notificationsender.exceptions.InvalidEmailEventPayloadException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static br.dev.notificationsender.events.contratos.enumx.EventType.FATURA_EMITIDA;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FaturaEmitidaEventValidatorTest {

    private final FaturaEmitidaEventValidator validator = new FaturaEmitidaEventValidator();

    @Test
    void deveAceitarPayloadValido() {
        assertThatCode(() -> validator.validate(validPayload())).doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarPayloadNulo() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(InvalidEmailEventPayloadException.class)
                .hasMessageContaining("não pode ser nulo");
    }

    @Test
    void deveRejeitarCamposObrigatoriosAusentes() {
        FaturaEmitidaEvent payload = new FaturaEmitidaEvent(null, null, null, null, null, null, null);

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(InvalidEmailEventPayloadException.class)
                .hasMessageContaining("eventId é obrigatório")
                .hasMessageContaining("eventType é obrigatório")
                .hasMessageContaining("numeroApartamento é obrigatório")
                .hasMessageContaining("valorTotal é obrigatório")
                .hasMessageContaining("dataVencimento é obrigatório")
                .hasMessageContaining("destinatário é obrigatório")
                .hasMessageContaining("faturaId é obrigatório");
    }

    @Test
    void deveRejeitarValoresInvalidos() {
        FaturaEmitidaEvent payload = new FaturaEmitidaEvent(
                0,
                BigDecimal.ZERO,
                LocalDate.parse("2024-01-01"),
                "email-invalido",
                0L,
                UUID.randomUUID(),
                FATURA_EMITIDA
        );

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(InvalidEmailEventPayloadException.class)
                .hasMessageContaining("numeroApartamento deve ser positivo")
                .hasMessageContaining("valorTotal deve ser maior que zero")
                .hasMessageContaining("destinatário deve ser um e-mail valido")
                .hasMessageContaining("faturaId deve ser positivo");
    }

    private static FaturaEmitidaEvent validPayload() {
        return new FaturaEmitidaEvent(
                101,
                BigDecimal.valueOf(250.75),
                LocalDate.parse("2024-01-01").plusDays(10),
                "morador@example.com",
                10L,
                UUID.randomUUID(),
                FATURA_EMITIDA
        );
    }

}
