package hexlet.code.exception;

import org.hibernate.exception.ConstraintViolationException;
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

/**
 * Global exception handler for all REST controllers.
 * Provides unified error responses for common runtime exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles integrity constraint violations such as foreign key or unique key errors.
     *
     * @param ex the thrown exception
     * @return response with appropriate error message and HTTP 422 status
     */
    @ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class})
    public ResponseEntity<Map<String, String>> handleDataIntegrity(Exception ex) {
        String message = "Data integrity violation";
        String causeMessage = "";

        if (ex instanceof DataIntegrityViolationException dataEx && dataEx.getMostSpecificCause() != null) {
            causeMessage = dataEx.getMostSpecificCause().getMessage();
        } else if (ex instanceof ConstraintViolationException constraintEx && constraintEx.getSQLException() != null) {
            causeMessage = constraintEx.getSQLException().getMessage();
        }

        if (causeMessage != null) {
            causeMessage = causeMessage.toLowerCase();

            if (causeMessage.contains("foreign key") && causeMessage.contains("tasks")
                    && causeMessage.contains("assignee")) {
                message = "Cannot delete user with tasks";
            } else if (causeMessage.contains("foreign key") && causeMessage.contains("task_statuses")) {
                message = "Cannot delete task status with tasks";
            } else if (causeMessage.contains("foreign key") && causeMessage.contains("labels")) {
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
     * Handles validation errors raised during request body binding and DTO validation.
     *
     * @param ex the thrown exception
     * @return response containing details of invalid fields and HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
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
     * Handles transaction-level data constraint violations.
     *
     * @param ex the thrown exception
     * @return response with HTTP 422 status
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, String>> handleTransaction(TransactionSystemException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("message", "Transaction failed: data constraint violation"));
    }

    /**
     * Handles exceptions explicitly thrown with {@link ResponseStatusException}.
     *
     * @param ex the thrown exception
     * @return response with predefined HTTP status and message
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("message", ex.getReason()));
    }

    /**
     * Handles lower-level SQL errors during database interaction.
     *
     * @param ex the thrown exception
     * @return response with SQL error details and HTTP 500 status
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, String>> handleSql(SQLException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Database error: " + ex.getMessage()));
    }

    /**
     * Handles all uncaught exceptions to provide a unified internal server error response.
     *
     * @param ex the thrown exception
     * @return response with generic error message and HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Internal server error"));
    }
}
