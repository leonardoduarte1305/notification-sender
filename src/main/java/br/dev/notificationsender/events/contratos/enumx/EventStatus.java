package br.dev.notificationsender.events.contratos.enumx;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventStatus {

    FINISHED("FINISHED"),

    FAILED("FAILED");

    private final String description;

}
