package pkibackend.pkibackend.exceptions;

public class InternalServerErrorException extends Exception{
    public InternalServerErrorException(String errorMessage)
    {
        super(errorMessage);
    }
}
