package br.dev.notificationsender.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class ErrorMessages {

    public static final String EVENTO_NAO_PODE_SER_NULO = "Evento não pode ser nulo";

}
