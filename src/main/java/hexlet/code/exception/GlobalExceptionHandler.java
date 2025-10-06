package hexlet.code.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles database integrity violations, such as attempts
     * to delete entities that are referenced by other records.
     *
     * @param ex the thrown {@link DataIntegrityViolationException}
     * @return response with error message and HTTP 422 status
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = "Cannot delete entity with tasks";

        String causeMessage = ex.getMostSpecificCause().getMessage();
        if (causeMessage != null) {
            if (causeMessage.contains("users")) {
                message = "Cannot delete user with tasks";
            } else if (causeMessage.contains("task_statuses")) {
                message = "Cannot delete task status with tasks";
            } else if (causeMessage.contains("labels")) {
                message = "Cannot delete label with tasks";
            }
        }

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("message", message));
    }
}
