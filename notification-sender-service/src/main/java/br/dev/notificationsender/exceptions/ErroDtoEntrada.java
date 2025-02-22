package br.dev.notificationsender.exceptions;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;


@Builder
@Jacksonized
@RequiredArgsConstructor
public class ErroDtoEntrada {

    private final String campo;

    private final String erro;

}
