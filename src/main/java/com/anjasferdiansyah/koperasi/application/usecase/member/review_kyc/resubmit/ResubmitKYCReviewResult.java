package com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.resubmit;

import com.anjasferdiansyah.koperasi.domain.model.member.MemberStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResubmitKYCReviewResult(
        UUID memberId,
        MemberStatus status,
        LocalDateTime kycReviewedAt,
        String kycReviewedBy,
        String kycReviewReason
) {
}
