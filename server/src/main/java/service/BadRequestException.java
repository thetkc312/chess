package service;

/**
 * Indicates there was an error with the provided request information
 */
public class BadRequestException extends Exception{
    public BadRequestException(String message) {
        super(message);
    }
    public BadRequestException(String message, Throwable ex) {
        super(message, ex);
    }
}
