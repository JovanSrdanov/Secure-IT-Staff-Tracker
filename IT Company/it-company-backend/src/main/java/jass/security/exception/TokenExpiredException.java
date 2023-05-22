package jass.security.exception;

public class TokenExpiredException extends Exception{
    public TokenExpiredException() {}
    public TokenExpiredException(String message) {
        super(message);
    }
}
