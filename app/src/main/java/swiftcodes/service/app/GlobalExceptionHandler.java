package swiftcodes.service.app;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorResponse> handleApiException(APIException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex.getErrorCode().equals("ERR-404")) {
            status = HttpStatus.NOT_FOUND;
        }
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        ex.printStackTrace();
        Map<String, String> errorResponse = new LinkedHashMap<>();
        errorResponse.put("error_code", "ERR-500");
        errorResponse.put("message", "An unexpected error occurred. Please try again later.");

        return ResponseEntity.internalServerError().body(errorResponse);
    }
}
