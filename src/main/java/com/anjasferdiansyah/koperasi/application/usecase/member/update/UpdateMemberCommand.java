package com.anjasferdiansyah.koperasi.application.usecase.member.update;

import java.util.UUID;

public record UpdateMemberCommand(
        UUID memberId,
        String fullName,
        String email,
        String address,
        String nik,
        String phoneNumber
) {
}
