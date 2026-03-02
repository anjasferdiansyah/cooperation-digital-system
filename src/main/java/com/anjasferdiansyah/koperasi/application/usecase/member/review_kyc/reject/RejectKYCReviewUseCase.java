package com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.reject;

public interface RejectKYCReviewUseCase {
    RejectKYCReviewResult execute(RejectKYCReviewCommand command);
}
