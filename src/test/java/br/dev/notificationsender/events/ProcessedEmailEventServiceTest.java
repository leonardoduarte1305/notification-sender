package br.dev.notificationsender.events;

import br.dev.notificationsender.events.contratos.enumx.EventStatus;
import br.dev.notificationsender.events.contratos.enumx.EventType;
import br.dev.notificationsender.events.entity.ProcessedEmailEvent;
import br.dev.notificationsender.events.repository.ProcessedEmailEventRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static br.dev.notificationsender.events.contratos.enumx.EventStatus.FAILED;
import static br.dev.notificationsender.events.contratos.enumx.EventStatus.FINISHED;
import static br.dev.notificationsender.events.contratos.enumx.EventStatus.PROCESSING;
import static br.dev.notificationsender.events.contratos.enumx.EventType.FATURA_EMITIDA;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessedEmailEventServiceTest {

    @Test
    void deveReservarEventoNovoParaProcessamento() {
        UUID eventId = UUID.randomUUID();
        RepositoryFake repository = new RepositoryFake(1, Optional.empty());
        ProcessedEmailEventService service = new ProcessedEmailEventService(repository.proxy());

        boolean reservado = service.reservarParaProcessamento(eventId, FATURA_EMITIDA);

        assertThat(reservado).isTrue();
        assertThat(repository.findByEventIdCalled).isFalse();
    }

    @Test
    void deveIgnorarEventoJaFinalizado() {
        UUID eventId = UUID.randomUUID();
        RepositoryFake repository = new RepositoryFake(0, Optional.of(evento(eventId, FINISHED)));
        ProcessedEmailEventService service = new ProcessedEmailEventService(repository.proxy());

        boolean reservado = service.reservarParaProcessamento(eventId, FATURA_EMITIDA);

        assertThat(reservado).isFalse();
        assertThat(repository.savedEvent).isNull();
    }

    @Test
    void deveIgnorarEventoEmProcessamentoParaEvitarEnvioDuplicado() {
        UUID eventId = UUID.randomUUID();
        RepositoryFake repository = new RepositoryFake(0, Optional.of(evento(eventId, PROCESSING)));
        ProcessedEmailEventService service = new ProcessedEmailEventService(repository.proxy());

        boolean reservado = service.reservarParaProcessamento(eventId, FATURA_EMITIDA);

        assertThat(reservado).isFalse();
        assertThat(repository.savedEvent).isNull();
    }

    @Test
    void deveReprocessarEventoComFalha() {
        UUID eventId = UUID.randomUUID();
        RepositoryFake repository = new RepositoryFake(0, Optional.of(evento(eventId, FAILED)));
        ProcessedEmailEventService service = new ProcessedEmailEventService(repository.proxy());

        boolean reservado = service.reservarParaProcessamento(eventId, FATURA_EMITIDA);

        assertThat(reservado).isTrue();
        assertThat(repository.savedEvent).isNotNull();
        assertThat(repository.savedEvent.getStatus()).isEqualTo(PROCESSING);
    }

    private static ProcessedEmailEvent evento(UUID eventId, EventStatus status) {
        return new ProcessedEmailEvent(eventId, EventType.FATURA_EMITIDA, status);
    }

    private static final class RepositoryFake {

        private final int reserveNewEventResult;

        private final Optional<ProcessedEmailEvent> findByEventIdResult;

        private boolean findByEventIdCalled;

        private ProcessedEmailEvent savedEvent;

        private RepositoryFake(int reserveNewEventResult, Optional<ProcessedEmailEvent> findByEventIdResult) {
            this.reserveNewEventResult = reserveNewEventResult;
            this.findByEventIdResult = findByEventIdResult;
        }

        private ProcessedEmailEventRepository proxy() {
            return (ProcessedEmailEventRepository) Proxy.newProxyInstance(
                    ProcessedEmailEventRepository.class.getClassLoader(),
                    new Class<?>[]{ProcessedEmailEventRepository.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "reserveNewEvent" -> reserveNewEvent((UUID) args[0], (String) args[1], (Instant) args[2]);
                        case "findByEventId" -> findByEventId((UUID) args[0]);
                        case "save" -> save((ProcessedEmailEvent) args[0]);
                        default -> throw new UnsupportedOperationException("Metodo nao implementado no fake: " + method.getName());
                    }
            );
        }

        private int reserveNewEvent(UUID eventId, String eventType, Instant processedAt) {
            assertThat(eventId).isNotNull();
            assertThat(eventType).isEqualTo(FATURA_EMITIDA.name());
            assertThat(processedAt).isNotNull();
            return reserveNewEventResult;
        }

        private Optional<ProcessedEmailEvent> findByEventId(UUID eventId) {
            assertThat(eventId).isNotNull();
            findByEventIdCalled = true;
            return findByEventIdResult;
        }

        private ProcessedEmailEvent save(ProcessedEmailEvent event) {
            savedEvent = event;
            return event;
        }

    }

}
