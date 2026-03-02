package com.anjasferdiansyah.koperasi.presentation.response;

import com.anjasferdiansyah.koperasi.domain.model.MemberStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApproveKycResponse(
        UUID memberId,
        MemberStatus status,
        LocalDateTime kycReviewedAt,
        String kycReviewedBy
) {
}
