package br.dev.notificationsender.exceptions;

public class EmailSendingFailureException extends RuntimeException {

    public EmailSendingFailureException(String message) {
        super(message);
    }

    public EmailSendingFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
