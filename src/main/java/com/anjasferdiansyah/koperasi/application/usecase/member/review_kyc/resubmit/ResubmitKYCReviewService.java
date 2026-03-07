package com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.resubmit;

import com.anjasferdiansyah.koperasi.domain.exception.MemberNotFoundException;
import com.anjasferdiansyah.koperasi.domain.model.member.Member;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResubmitKYCReviewService implements ResubmitKYCReviewUseCase {

    private final MemberRepository memberRepository;

    public ResubmitKYCReviewService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public ResubmitKYCReviewResult execute(ResubmitKYCReviewCommand command) {
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new MemberNotFoundException(command.memberId()));

        member.resubmitKyc();
        Member saved = memberRepository.save(member);

        return new ResubmitKYCReviewResult(
                saved.getId(),
                saved.getStatus(),
                saved.getKycReviewedAt(),
                saved.getKycReviewedBy(),
                saved.getKycReviewReason()
        );
    }
}
