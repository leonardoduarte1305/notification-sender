package br.dev.notificationsender.events.contratos;

import br.dev.notificationsender.events.contratos.enumx.EventType;

import java.util.UUID;

public interface EmailEvent {

    UUID getEventId();

    EventType getEventType();

}
