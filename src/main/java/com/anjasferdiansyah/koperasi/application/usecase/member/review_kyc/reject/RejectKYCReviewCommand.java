package com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.reject;

import java.util.UUID;

public record RejectKYCReviewCommand(UUID memberId, String reviewer, String reviewReason) {
}
