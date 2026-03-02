package com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.reject;

import com.anjasferdiansyah.koperasi.domain.model.MemberStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RejectKYCReviewResult (
        UUID memberId,
        MemberStatus status,
        LocalDateTime kycReviewedAt,
        String kycReviewedBy){
}
