package server;

public class StatusReader {
    public enum ResponseStatus {
        GOOD,
        BAD_REQUEST,
        UNAUTHORIZED,
        ALREADY_TAKEN,
        SERVER_ERROR,
        NO_CONNECTION,
        UNKNOWN_ERROR
    }

    public static int getCodeFromStatus(ResponseStatus responseStatus) {
        return switch (responseStatus) {
            case ResponseStatus.GOOD -> 200;
            case ResponseStatus.BAD_REQUEST -> 400;
            case ResponseStatus.UNAUTHORIZED -> 401;
            case ResponseStatus.ALREADY_TAKEN -> 403;
            case ResponseStatus.SERVER_ERROR -> 500;
            default -> 0;
        };
    }

    public static ResponseStatus getStatusFromCode(int responseCode) {
        return switch (responseCode) {
            case 200 -> ResponseStatus.GOOD;
            case 400 -> ResponseStatus.BAD_REQUEST;
            case 401 -> ResponseStatus.UNAUTHORIZED;
            case 403 -> ResponseStatus.ALREADY_TAKEN;
            case 500 -> ResponseStatus.SERVER_ERROR;
            default -> ResponseStatus.UNKNOWN_ERROR;
        };
    }
}
