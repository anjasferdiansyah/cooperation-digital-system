package com.anjasferdiansyah.koperasi.presentation.response;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OpenSavingsAccountRequest(
        @NotNull(message = "memberId is required")
        UUID memberId,

        @NotNull(message = "savingsType is required")
        SavingsType savingsType
) {
}
