package com.anjasferdiansyah.koperasi.application.usecase.member.update;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateMemberResult (
        UUID id,
        String fullName,
        String email,
        String address,
        String nik,
        String phoneNumber,
        LocalDateTime registeredAt
){
}
