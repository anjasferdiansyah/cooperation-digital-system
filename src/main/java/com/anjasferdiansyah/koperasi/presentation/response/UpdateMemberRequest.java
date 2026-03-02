package com.anjasferdiansyah.koperasi.presentation.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateMemberRequest(
        @NotBlank(message = "fullName is required")
        String fullName,

        @NotBlank(message = "email is required")
        @Email(message = "email format is invalid")
        String email,

        @NotBlank(message = "address is required")
        String address,

        @NotBlank(message = "nik is required")
        @Pattern(regexp = "^\\d{16}$", message = "nik must be 16 digits")
        String nik,

        @NotBlank(message = "phoneNumber is required")
        @Pattern(regexp = "^\\+?[0-9]{9,20}$", message = "phoneNumber format is invalid")
        String phoneNumber
) {
}
