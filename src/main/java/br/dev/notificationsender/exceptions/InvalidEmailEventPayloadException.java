package br.dev.notificationsender.exceptions;

public class InvalidEmailEventPayloadException extends RuntimeException {

    public InvalidEmailEventPayloadException(String message) {
        super(message);
    }

}
