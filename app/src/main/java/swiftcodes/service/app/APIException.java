package swiftcodes.service.app;

public class APIException extends RuntimeException {
    private final String errorCode;

    public APIException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
