package com.anjasferdiansyah.koperasi.presentation.response;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public record ApiResponseWrapper<T>(
        boolean success,
        String message,
        T data,
        ApiErrorResponse error,
        LocalDateTime timestamp
) {

    public static <T> ApiResponseWrapper<T> success(String message, T data) {
        return new ApiResponseWrapper<>(true, message, data, null, LocalDateTime.now(ZoneOffset.UTC));
    }

    public static <T> ApiResponseWrapper<T> failure(String message, ApiErrorResponse error) {
        return new ApiResponseWrapper<>(false, message, null, error, LocalDateTime.now(ZoneOffset.UTC));
    }
}
