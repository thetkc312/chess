package server;

public class ResponseException extends RuntimeException {

    final public int responseCode;
    final public StatusReader.ResponseStatus responseStatus;

    public ResponseException(StatusReader.ResponseStatus responseStatus, String message) {
        super(message);
        this.responseCode = StatusReader.getCodeFromStatus(responseStatus);
        this.responseStatus = responseStatus;
    }

    public ResponseException(int responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
        this.responseStatus = StatusReader.getStatusFromCode(responseCode);
    }

}
