package jass.security.exception;

public class EmailTakenException extends Exception {
    public EmailTakenException(String message) {
        super(message);
    }
    public EmailTakenException() {
    }
}
