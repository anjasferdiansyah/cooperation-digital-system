package com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.approve;

import com.anjasferdiansyah.koperasi.domain.exception.MemberNotFoundException;
import com.anjasferdiansyah.koperasi.domain.model.Member;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@AllArgsConstructor
public class ApproveKYCReviewService implements ApproveKYCReviewUseCase {

    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public ApproveKYCReviewResult execute(ApproveKYCReviewCommand command) {
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new MemberNotFoundException(command.memberId()));

        member.approveKyc(command.reviewer(), LocalDateTime.now(ZoneOffset.UTC));
        Member saved = memberRepository.save(member);

        return new ApproveKYCReviewResult(
                saved.getId(),
                saved.getStatus(),
                saved.getKycReviewedAt(),
                saved.getKycReviewedBy()
        );
    }
}
