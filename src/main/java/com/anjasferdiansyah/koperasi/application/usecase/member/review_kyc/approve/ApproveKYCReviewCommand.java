package com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.approve;

import java.util.UUID;

public record ApproveKYCReviewCommand(UUID memberId, String reviewer) {
}
