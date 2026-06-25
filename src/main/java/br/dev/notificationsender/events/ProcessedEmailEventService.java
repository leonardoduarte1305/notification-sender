package br.dev.notificationsender.events;

import br.dev.notificationsender.events.contratos.enumx.EventStatus;
import br.dev.notificationsender.events.contratos.enumx.EventType;
import br.dev.notificationsender.events.entity.ProcessedEmailEvent;
import br.dev.notificationsender.events.repository.ProcessedEmailEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

import static br.dev.notificationsender.events.contratos.enumx.EventStatus.FINISHED;
import static br.dev.notificationsender.events.contratos.enumx.EventStatus.PROCESSING;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessedEmailEventService {

    private final ProcessedEmailEventRepository repository;

    public boolean reservarParaProcessamento(UUID eventId, EventType eventType) {
        int eventosReservados = repository.reserveNewEvent(eventId, eventType.name(), Instant.now());

        if (eventosReservados == 1) {
            return true;
        }

        ProcessedEmailEvent eventoExistente = repository.findByEventId(eventId)
                .orElseThrow(() -> new IllegalStateException("Evento ja existia na reserva, mas nao foi encontrado: " + eventId));

        if (finishedShouldReturn(eventoExistente)) {
            log.info("Evento de e-mail ja finalizado. eventId={}, eventType={}", eventId, eventType);
            return false;
        }

        if (processingShouldReturn(eventoExistente)) {
            log.info("Evento de e-mail ja esta em processamento. eventId={}, eventType={}", eventId, eventType);
            return false;
        }

        eventoExistente.setStatus(PROCESSING);
        eventoExistente.setEventType(eventType);
        eventoExistente.setProcessedAt(Instant.now());
        repository.save(eventoExistente);
        return true;
    }

    private static boolean processingShouldReturn(ProcessedEmailEvent eventoExistente) {
        return PROCESSING.equals(eventoExistente.getStatus());
    }

    private static boolean finishedShouldReturn(ProcessedEmailEvent eventoExistente) {
        return FINISHED.equals(eventoExistente.getStatus());
    }

    public void marcarComoFinalizado(UUID eventId, EventType eventType) {
        ProcessedEmailEvent eventoProcessado = buscarEvento(eventId);
        eventoProcessado.marcarComoFinalizado(eventType);
        repository.save(eventoProcessado);
    }

    public void marcarComoFalha(UUID eventId, EventType eventType) {
        ProcessedEmailEvent eventoProcessado = buscarEvento(eventId);
        eventoProcessado.marcarComoFalha(eventType);
        repository.save(eventoProcessado);
    }

    private ProcessedEmailEvent buscarEvento(UUID eventId) {
        return repository.findByEventId(eventId)
                .orElseThrow(() -> new IllegalStateException("Evento reservado nao encontrado: " + eventId));
    }

}
