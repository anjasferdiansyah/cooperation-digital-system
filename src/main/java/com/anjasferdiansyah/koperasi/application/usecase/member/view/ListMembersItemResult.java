package com.anjasferdiansyah.koperasi.application.usecase.member.view;

import com.anjasferdiansyah.koperasi.domain.model.member.MemberStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ListMembersItemResult(
        UUID id,
        String fullName,
        String email,
        String address,
        String nik,
        String phoneNumber,
        MemberStatus status,
        LocalDateTime registeredAt,
        LocalDateTime kycReviewedAt,
        String kycReviewedBy
) {
}
