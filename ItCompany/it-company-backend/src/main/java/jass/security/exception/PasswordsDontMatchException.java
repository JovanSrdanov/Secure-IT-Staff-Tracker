package jass.security.exception;

public class PasswordsDontMatchException extends Exception {
    public PasswordsDontMatchException(String message){
        super(message);
    }
}
