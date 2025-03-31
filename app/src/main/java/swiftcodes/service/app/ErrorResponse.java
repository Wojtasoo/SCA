package swiftcodes.service.app;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ErrorResponse {
    @JsonProperty("error_code")
    private String errorCode;
    @JsonProperty("message")
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

