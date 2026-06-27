package br.dev.notificationsender.exceptions;

public class InvalidEmailEventPayloadException extends NonRetryableMessageException {

    public InvalidEmailEventPayloadException(String message) {
        super(message);
    }

}
