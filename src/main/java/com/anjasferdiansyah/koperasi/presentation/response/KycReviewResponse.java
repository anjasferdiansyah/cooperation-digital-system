package com.anjasferdiansyah.koperasi.presentation.response;

import com.anjasferdiansyah.koperasi.domain.model.member.MemberStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record KycReviewResponse(
        UUID memberId,
        MemberStatus status,
        LocalDateTime kycReviewedAt,
        String kycReviewedBy,
        String kycReviewReason
) {
}
