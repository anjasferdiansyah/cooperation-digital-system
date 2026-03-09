package com.anjasferdiansyah.koperasi.presentation.response;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateSavingsTransactionRequest(
        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "amount must be greater than zero")
        BigDecimal amount,

        @Size(max = 60, message = "externalReference max length is 60")
        String externalReference,

        @Size(max = 255, message = "note max length is 255")
        String note,

        LocalDateTime occurredAt
) {
}
