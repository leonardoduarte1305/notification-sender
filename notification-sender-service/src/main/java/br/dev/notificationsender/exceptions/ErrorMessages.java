package br.dev.notificationsender.exceptions;

import lombok.Getter;

@Getter
public class ErrorMessages {

    public static final String MISSING_X_API_KEY = "x-api-key is missing";

    public static final String INVALID_X_API_KEY = "invalid x-api-key";

    public static final String EVENTO_NAO_PODE_SER_NULO = "Evento não pode ser nulo";

}
