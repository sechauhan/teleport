package com.example.teleport.exception;

import com.example.teleport.config.OptimizerConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = extractFieldErrors(ex);
        boolean isPayloadTooLarge = isPayloadTooLargeError(ex);
        HttpStatus status = determineHttpStatus(isPayloadTooLarge);
        Map<String, Object> errorResponse = buildErrorResponse(fieldErrors, status, isPayloadTooLarge);
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    private Map<String, String> extractFieldErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        return fieldErrors;
    }

    private boolean isPayloadTooLargeError(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .anyMatch(this::isOrdersSizeLimitError);
    }

    private boolean isOrdersSizeLimitError(FieldError error) {
        return "orders".equals(error.getField()) 
                && error.getDefaultMessage() != null
                && error.getDefaultMessage().contains("maximum limit");
    }

    private HttpStatus determineHttpStatus(boolean isPayloadTooLarge) {
        return isPayloadTooLarge ? HttpStatus.PAYLOAD_TOO_LARGE : HttpStatus.BAD_REQUEST;
    }

    private Map<String, Object> buildErrorResponse(
            Map<String, String> fieldErrors, 
            HttpStatus status, 
            boolean isPayloadTooLarge
    ) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errors", fieldErrors);
        errorResponse.put("status", status.value());
        errorResponse.put("message", buildErrorMessage(isPayloadTooLarge));
        return errorResponse;
    }

    private String buildErrorMessage(boolean isPayloadTooLarge) {
        return isPayloadTooLarge 
                ? "Number of orders exceeds the maximum limit of " + OptimizerConstants.MAX_ORDERS_LIMIT
                : "Validation failed";
    }
}
