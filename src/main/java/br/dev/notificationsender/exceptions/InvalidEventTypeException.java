package br.dev.notificationsender.exceptions;

public class InvalidEventTypeException extends RuntimeException {

    public InvalidEventTypeException(String message) {
        super(message);
    }

}
