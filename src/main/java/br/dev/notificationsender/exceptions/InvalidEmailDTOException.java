package br.dev.notificationsender.exceptions;

public class InvalidEmailDTOException extends NonRetryableMessageException {

    public InvalidEmailDTOException(String message) {
        super(message);
    }

}
