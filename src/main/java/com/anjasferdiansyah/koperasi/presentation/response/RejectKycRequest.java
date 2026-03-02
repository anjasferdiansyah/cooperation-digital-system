package com.anjasferdiansyah.koperasi.presentation.response;

import jakarta.validation.constraints.NotBlank;

public record RejectKycRequest(
        @NotBlank(message = "reviewer is required")
        String reviewer,
        @NotBlank(message = "reviewReason is required")
        String reviewReason
) {
}
