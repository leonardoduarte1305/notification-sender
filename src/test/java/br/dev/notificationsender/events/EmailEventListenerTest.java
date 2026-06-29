package br.dev.notificationsender.events;

import br.dev.notificationsender.events.contratos.EmailDTO;
import br.dev.notificationsender.events.contratos.FaturaEmitidaEvent;
import br.dev.notificationsender.events.contratos.enumx.EventType;
import br.dev.notificationsender.events.validation.FaturaEmitidaEventValidator;
import br.dev.notificationsender.exceptions.EmailSendingFailureException;
import br.dev.notificationsender.exceptions.InvalidEmailDTOException;
import br.dev.notificationsender.service.EmailService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static br.dev.notificationsender.events.contratos.enumx.EventType.FATURA_EMITIDA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailEventListenerTest {

    @Test
    void deveEnviarEmailEMarcarEventoComoFinalizadoQuandoEventoForReservado() {
        FaturaEmitidaEvent payload = validPayload();
        EmailServiceFake emailService = new EmailServiceFake();
        ProcessedEmailEventServiceFake processedEmailEventService = new ProcessedEmailEventServiceFake(true);
        EmailEventListener listener = listener(emailService, processedEmailEventService);

        listener.consumirEmissaoDeFaturas(payload);

        assertThat(emailService.sentEmail).isNotNull();
        assertThat(emailService.sentEmail.getTo()).containsExactly(payload.destinatario());
        assertThat(processedEmailEventService.finalizedEventId).isEqualTo(payload.eventId());
        assertThat(processedEmailEventService.failedEventId).isNull();
    }

    @Test
    void deveIgnorarEventoQuandoReservaNaoForObtida() {
        FaturaEmitidaEvent payload = validPayload();
        EmailServiceFake emailService = new EmailServiceFake();
        ProcessedEmailEventServiceFake processedEmailEventService = new ProcessedEmailEventServiceFake(false);
        EmailEventListener listener = listener(emailService, processedEmailEventService);

        listener.consumirEmissaoDeFaturas(payload);

        assertThat(emailService.sentEmail).isNull();
        assertThat(processedEmailEventService.finalizedEventId).isNull();
        assertThat(processedEmailEventService.failedEventId).isNull();
    }

    @Test
    void deveMarcarFalhaERelancarQuandoEnvioFalharAposReserva() {
        FaturaEmitidaEvent payload = validPayload();
        EmailServiceFake emailService = new EmailServiceFake();
        emailService.exceptionToThrow = new EmailSendingFailureException("falha smtp");
        ProcessedEmailEventServiceFake processedEmailEventService = new ProcessedEmailEventServiceFake(true);
        EmailEventListener listener = listener(emailService, processedEmailEventService);

        assertThatThrownBy(() -> listener.consumirEmissaoDeFaturas(payload))
                .isInstanceOf(EmailSendingFailureException.class);

        assertThat(processedEmailEventService.finalizedEventId).isNull();
        assertThat(processedEmailEventService.failedEventId).isEqualTo(payload.eventId());
    }

    @Test
    void deveMarcarFalhaERelancarQuandoErroForNaoRecuperavelAposReserva() {
        FaturaEmitidaEvent payload = validPayload();
        EmailServiceFake emailService = new EmailServiceFake();
        emailService.exceptionToThrow = new InvalidEmailDTOException("dto invalido");
        ProcessedEmailEventServiceFake processedEmailEventService = new ProcessedEmailEventServiceFake(true);
        EmailEventListener listener = listener(emailService, processedEmailEventService);

        assertThatThrownBy(() -> listener.consumirEmissaoDeFaturas(payload))
                .isInstanceOf(InvalidEmailDTOException.class);

        assertThat(processedEmailEventService.finalizedEventId).isNull();
        assertThat(processedEmailEventService.failedEventId).isEqualTo(payload.eventId());
    }

    private static EmailEventListener listener(EmailService emailService, ProcessedEmailEventService processedEmailEventService) {
        return new EmailEventListener(emailService, processedEmailEventService, new FaturaEmitidaEventValidator());
    }

    private static FaturaEmitidaEvent validPayload() {
        return new FaturaEmitidaEvent(
                101,
                BigDecimal.valueOf(250.75),
                LocalDate.parse("2026-07-10"),
                "morador@example.com",
                10L,
                UUID.randomUUID(),
                FATURA_EMITIDA
        );
    }

    private static final class EmailServiceFake extends EmailService {

        private EmailDTO sentEmail;

        private RuntimeException exceptionToThrow;

        private EmailServiceFake() {
            super(null, null, null);
        }

        @Override
        public void enviarEmail(EmailDTO emailDTO) {
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }

            sentEmail = emailDTO;
        }
    }

    private static final class ProcessedEmailEventServiceFake extends ProcessedEmailEventService {

        private final boolean shouldReserve;

        private UUID finalizedEventId;

        private UUID failedEventId;

        private ProcessedEmailEventServiceFake(boolean shouldReserve) {
            super(null);
            this.shouldReserve = shouldReserve;
        }

        @Override
        public boolean reservarParaProcessamento(UUID eventId, EventType eventType) {
            return shouldReserve;
        }

        @Override
        public void marcarComoFinalizado(UUID eventId, EventType eventType) {
            finalizedEventId = eventId;
        }

        @Override
        public void marcarComoFalha(UUID eventId, EventType eventType) {
            failedEventId = eventId;
        }
    }
}
