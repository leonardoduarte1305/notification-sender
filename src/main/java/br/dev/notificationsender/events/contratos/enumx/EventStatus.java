package br.dev.notificationsender.events.contratos.enumx;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventStatus {

    PROCESSING("PROCESSING"),

    FINISHED("FINISHED"),

    FAILED("FAILED");

    private final String description;

}
