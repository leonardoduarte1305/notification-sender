package br.dev.notificationsender.exceptions;

public class NonRetryableMessageException extends RuntimeException {

    public NonRetryableMessageException(String message) {
        super(message);
    }

}
