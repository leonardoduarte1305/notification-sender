package br.dev.notificationsender.events.contratos;

import br.dev.notificationsender.events.contratos.enumx.EventType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FaturaEmitidaEvent(Integer numeroApartamento,
                                 BigDecimal valorTotal,
                                 LocalDate dataVencimento,
                                 String destinatario,
                                 Long faturaId,
                                 UUID eventId,
                                 EventType eventType) implements EmailEvent {
    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

}
