package dataaccess;

public class InvalidCredentialsException extends DataAccessException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
    public InvalidCredentialsException(String message, Throwable ex) {
        super(message, ex);
    }
}
