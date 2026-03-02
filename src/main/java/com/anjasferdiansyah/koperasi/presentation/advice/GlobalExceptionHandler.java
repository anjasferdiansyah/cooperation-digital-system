package com.anjasferdiansyah.koperasi.presentation.advice;

import com.anjasferdiansyah.koperasi.domain.exception.DomainValidationException;
import com.anjasferdiansyah.koperasi.domain.exception.DuplicateMemberEmailException;
import com.anjasferdiansyah.koperasi.domain.exception.DuplicateMemberNikException;
import com.anjasferdiansyah.koperasi.domain.exception.MemberNotFoundException;
import com.anjasferdiansyah.koperasi.presentation.response.ApiErrorResponse;
import com.anjasferdiansyah.koperasi.presentation.response.ApiResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateMemberEmailException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleDuplicateMemberEmail(DuplicateMemberEmailException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                "MEMBER_EMAIL_ALREADY_EXISTS",
                ex.getMessage(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseWrapper.failure("Request failed", error));
    }

    @ExceptionHandler(DuplicateMemberNikException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleDuplicateMemberNik(DuplicateMemberNikException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                "MEMBER_NIK_ALREADY_EXISTS",
                ex.getMessage(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseWrapper.failure("Request failed", error));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleMemberNotFound(MemberNotFoundException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                "MEMBER_NOT_FOUND",
                ex.getMessage(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.failure("Request failed", error));
    }

    @ExceptionHandler({DomainValidationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponseWrapper<Void>> handleBadRequest(Exception ex) {
        List<String> details = extractValidationDetails(ex);
        ApiErrorResponse error = new ApiErrorResponse(
                "BAD_REQUEST",
                ex.getMessage(),
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseWrapper.failure("Request failed", error));
    }

    private List<String> extractValidationDetails(Exception ex) {
        if (!(ex instanceof MethodArgumentNotValidException validationException)) {
            return List.of();
        }

        return validationException.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toMessage)
                .toList();
    }

    private String toMessage(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
