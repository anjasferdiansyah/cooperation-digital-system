package com.anjasferdiansyah.koperasi.application.usecase.member.create;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateMemberResult(
        UUID id,
        String fullName,
        String email,
        String address,
        String nik,
        String phoneNumber,
        LocalDateTime registeredAt
) {
}
