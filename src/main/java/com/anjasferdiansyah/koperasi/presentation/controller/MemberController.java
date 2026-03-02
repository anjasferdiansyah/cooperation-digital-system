package com.anjasferdiansyah.koperasi.presentation.controller;

import com.anjasferdiansyah.koperasi.application.usecase.member.create.CreateMemberCommand;
import com.anjasferdiansyah.koperasi.application.usecase.member.create.CreateMemberResult;
import com.anjasferdiansyah.koperasi.application.usecase.member.create.CreateMemberUseCase;
import com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.approve.ApproveKYCReviewCommand;
import com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.approve.ApproveKYCReviewResult;
import com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.approve.ApproveKYCReviewUseCase;
import com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.reject.RejectKYCReviewCommand;
import com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.reject.RejectKYCReviewResult;
import com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.reject.RejectKYCReviewUseCase;
import com.anjasferdiansyah.koperasi.presentation.response.ApproveKycRequest;
import com.anjasferdiansyah.koperasi.presentation.response.ApproveKycResponse;
import com.anjasferdiansyah.koperasi.presentation.response.ApiResponseWrapper;
import com.anjasferdiansyah.koperasi.presentation.response.CreateMemberRequest;
import com.anjasferdiansyah.koperasi.presentation.response.CreateMemberResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final CreateMemberUseCase createMemberUseCase;
    private final ApproveKYCReviewUseCase approveKYCReviewUseCase;
    private final RejectKYCReviewUseCase rejectKYCReviewUseCase;

    public MemberController(CreateMemberUseCase createMemberUseCase,
                            ApproveKYCReviewUseCase approveKYCReviewUseCase,
                            RejectKYCReviewUseCase rejectKYCReviewUseCase) {
        this.createMemberUseCase = createMemberUseCase;
        this.approveKYCReviewUseCase = approveKYCReviewUseCase;
        this.rejectKYCReviewUseCase = rejectKYCReviewUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponseWrapper<CreateMemberResponse>> create(@Valid @RequestBody CreateMemberRequest request) {
        CreateMemberResult result = createMemberUseCase.execute(
                new CreateMemberCommand(
                        request.fullName(),
                        request.email(),
                        request.address(),
                        request.nik(),
                        request.phoneNumber()
                )
        );

        CreateMemberResponse response = new CreateMemberResponse(
                result.id(),
                result.fullName(),
                result.email(),
                result.address(),
                result.nik(),
                result.phoneNumber(),
                result.registeredAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.success("Member created successfully", response));
    }

    @PatchMapping("/{memberId}/kyc/approve")
    public ResponseEntity<ApiResponseWrapper<ApproveKycResponse>> approveKyc(
            @PathVariable UUID memberId,
            @Valid @RequestBody ApproveKycRequest request) {

        ApproveKYCReviewResult result = approveKYCReviewUseCase.execute(
                new ApproveKYCReviewCommand(memberId, request.reviewer())
        );

        ApproveKycResponse response = new ApproveKycResponse(
                result.memberId(),
                result.status(),
                result.kycReviewedAt(),
                result.kycReviewedBy()
        );

        return ResponseEntity.ok(ApiResponseWrapper.success("KYC approved successfully", response));
    }

    @PatchMapping("/{memberId}/kyc/reject")
    public ResponseEntity<ApiResponseWrapper<ApproveKycResponse>> rejectKyc(
            @PathVariable UUID memberId,
            @Valid @RequestBody ApproveKycRequest request) {

        RejectKYCReviewResult result = rejectKYCReviewUseCase.execute(
                new RejectKYCReviewCommand(memberId, request.reviewer())
        );

        ApproveKycResponse response = new ApproveKycResponse(
                result.memberId(),
                result.status(),
                result.kycReviewedAt(),
                result.kycReviewedBy()
        );

        return ResponseEntity.ok(ApiResponseWrapper.success("KYC rejected successfully", response));
    }
}
