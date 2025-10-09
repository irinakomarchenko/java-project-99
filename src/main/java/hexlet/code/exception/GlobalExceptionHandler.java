package hexlet.code.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.HashMap;
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
        String message = "Data integrity violation";

        String causeMessage = ex.getMostSpecificCause().getMessage();
        if (causeMessage != null) {
            if (causeMessage.contains("users")) {
                message = "Cannot delete user with tasks";
            } else if (causeMessage.contains("task_statuses")) {
                message = "Cannot delete task status with tasks";
            } else if (causeMessage.contains("labels")) {
                message = "Cannot delete label with tasks";
            } else if (causeMessage.contains("unique") || causeMessage.contains("duplicate")) {
                message = "Duplicate record or unique constraint violation";
            } else if (causeMessage.contains("null")) {
                message = "Required field is missing";
            }
        }

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("message", message));
    }

    /**
     * Handles validation errors that occur when validating request DTOs.
     *
     * @param ex the thrown {@link MethodArgumentNotValidException}
     * @return response with details of invalid fields and HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(final MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "message", "Validation failed",
                        "details", errors.toString()
                ));
    }

    /**
     * Handles transaction system exceptions that can occur
     * during persistence operations.
     *
     * @param ex the thrown {@link TransactionSystemException}
     * @return response with error message and HTTP 422 status
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, String>> handleTransaction(final TransactionSystemException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("message", "Transaction failed: data constraint violation"));
    }

    /**
     * Handles exceptions explicitly thrown as {@link ResponseStatusException}
     * from service or controller layers.
     *
     * @param ex the thrown {@link ResponseStatusException}
     * @return response with the same status and reason as defined in the exception
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(final ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("message", ex.getReason()));
    }

    /**
     * Handles lower-level SQL exceptions that may occur during
     * database access or statement execution.
     *
     * @param ex the thrown {@link SQLException}
     * @return response with SQL error details and HTTP 500 status
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, String>> handleSql(final SQLException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Database error: " + ex.getMessage()));
    }

    /**
     * Handles all uncaught exceptions to prevent application crash
     * and provide a generic internal server error response.
     *
     * @param ex the thrown {@link Exception}
     * @return response with generic error message and HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(final Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Internal server error"));
    }
}
