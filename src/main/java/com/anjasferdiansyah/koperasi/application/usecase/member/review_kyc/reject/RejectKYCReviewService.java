package com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.reject;

import com.anjasferdiansyah.koperasi.domain.exception.MemberNotFoundException;
import com.anjasferdiansyah.koperasi.domain.model.Member;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class RejectKYCReviewService implements RejectKYCReviewUseCase {

    private final MemberRepository memberRepository;

    public RejectKYCReviewService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public RejectKYCReviewResult execute(RejectKYCReviewCommand command) {
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new MemberNotFoundException(command.memberId()));

        member.rejectKyc(command.reviewer(), LocalDateTime.now(ZoneOffset.UTC));
        Member saved = memberRepository.save(member);

        return new RejectKYCReviewResult(
                saved.getId(),
                saved.getStatus(),
                saved.getKycReviewedAt(),
                saved.getKycReviewedBy()
        );
    }
}
