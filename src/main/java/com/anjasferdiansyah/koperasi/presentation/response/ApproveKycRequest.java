package com.anjasferdiansyah.koperasi.presentation.response;

import jakarta.validation.constraints.NotBlank;

public record ApproveKycRequest(
        @NotBlank(message = "reviewer is required")
        String reviewer
) {
}
