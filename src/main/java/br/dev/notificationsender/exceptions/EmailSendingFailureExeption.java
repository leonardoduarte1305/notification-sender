package br.dev.notificationsender.exceptions;

public class EmailSendingFailureExeption extends RuntimeException {

    public EmailSendingFailureExeption(String message) {
        super(message);
    }

}
