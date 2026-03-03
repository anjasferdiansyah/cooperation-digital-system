package com.anjasferdiansyah.koperasi.presentation.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateMemberResponse(
        UUID id,
        String fullName,
        String email,
        String address,
        String nik,
        String phoneNumber,
        LocalDateTime registeredAt
) {
}
