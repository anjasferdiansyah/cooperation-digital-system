package com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.approve;

import com.anjasferdiansyah.koperasi.domain.model.member.MemberStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApproveKYCReviewResult(
        UUID memberId,
        MemberStatus status,
        LocalDateTime kycReviewedAt,
        String kycReviewedBy
) {
}
