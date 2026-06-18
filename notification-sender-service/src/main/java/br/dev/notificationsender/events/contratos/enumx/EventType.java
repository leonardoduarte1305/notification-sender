package br.dev.notificationsender.events.contratos.enumx;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

    FATURA_EMITIDA("FATURA_EMITIDA");

    private final String description;

}
