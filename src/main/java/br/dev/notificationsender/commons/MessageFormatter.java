package br.dev.notificationsender.commons;

public interface MessageFormatter {

    String getMessage();

    default String format(Object... args) {
        return String.format(getMessage(), args);
    }

}
