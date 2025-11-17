package com.auth.common.error;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth.common.response.ErrorResponse;
import com.auth.common.utils.ErrorCode;
import com.auth.common.utils.ResponseUtil;

@RestControllerAdvice
@Slf4j(topic = "GLOBAL-HANDLER-EXCEPTION")
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.warn("[handleBusinessException] {}", ex.getMessage());

        return ResponseUtil.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        log.warn("[handleValidationException] {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        log.debug("[Validation Error] {}", errors);

        return ResponseUtil.error(
                ErrorCode.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR.getMessage(), errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex) {
        log.warn("[handleConstraintViolation] {}", ex.getMessage());

        Map<String, String> violations = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(v -> violations.put(v.getPropertyPath().toString(), v.getMessage()));

        log.debug("[ConstraintViolation] {}", violations);

        return ResponseUtil.error(
                ErrorCode.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR.getMessage(), violations);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("[handleGenericException]", ex);

        return ResponseUtil.error(ErrorCode.INTERNAL_ERROR, ex.getMessage());
    }
}
