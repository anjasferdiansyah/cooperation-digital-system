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
import com.anjasferdiansyah.koperasi.application.usecase.member.update.UpdateMemberCommand;
import com.anjasferdiansyah.koperasi.application.usecase.member.update.UpdateMemberResult;
import com.anjasferdiansyah.koperasi.application.usecase.member.update.UpdateMemberUseCase;
import com.anjasferdiansyah.koperasi.application.usecase.member.view.ListMembersCommand;
import com.anjasferdiansyah.koperasi.application.usecase.member.view.ListMembersItemResult;
import com.anjasferdiansyah.koperasi.application.usecase.member.view.ListMembersUseCase;
import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;
import com.anjasferdiansyah.koperasi.presentation.response.ApiMetaResponse;
import com.anjasferdiansyah.koperasi.presentation.response.ApproveKycRequest;
import com.anjasferdiansyah.koperasi.presentation.response.ApiResponseWrapper;
import com.anjasferdiansyah.koperasi.presentation.response.CreateMemberRequest;
import com.anjasferdiansyah.koperasi.presentation.response.CreateMemberResponse;
import com.anjasferdiansyah.koperasi.presentation.response.KycReviewResponse;
import com.anjasferdiansyah.koperasi.presentation.response.MemberListItemResponse;
import com.anjasferdiansyah.koperasi.presentation.response.RejectKycRequest;
import com.anjasferdiansyah.koperasi.presentation.response.UpdateMemberRequest;
import com.anjasferdiansyah.koperasi.presentation.response.UpdateMemberResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/members")
public class MemberController {

    private final CreateMemberUseCase createMemberUseCase;
    private final UpdateMemberUseCase updateMemberUseCase;
    private final ApproveKYCReviewUseCase approveKYCReviewUseCase;
    private final RejectKYCReviewUseCase rejectKYCReviewUseCase;
    private final ListMembersUseCase listMembersUseCase;

    public MemberController(CreateMemberUseCase createMemberUseCase,
                            UpdateMemberUseCase updateMemberUseCase,
                            ApproveKYCReviewUseCase approveKYCReviewUseCase,
                            RejectKYCReviewUseCase rejectKYCReviewUseCase,
                            ListMembersUseCase listMembersUseCase) {
        this.createMemberUseCase = createMemberUseCase;
        this.updateMemberUseCase = updateMemberUseCase;
        this.approveKYCReviewUseCase = approveKYCReviewUseCase;
        this.rejectKYCReviewUseCase = rejectKYCReviewUseCase;
        this.listMembersUseCase = listMembersUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<MemberListItemResponse>>> listMembers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "registeredAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PageResult<ListMembersItemResult> result = listMembersUseCase.execute(
                new ListMembersCommand(search, page, size, sortBy, sortDir)
        );

        List<MemberListItemResponse> response = result.items().stream()
                .map(item -> new MemberListItemResponse(
                        item.id(),
                        item.fullName(),
                        item.email(),
                        item.address(),
                        item.nik(),
                        item.phoneNumber(),
                        item.status(),
                        item.registeredAt(),
                        item.kycReviewedAt(),
                        item.kycReviewedBy()
                ))
                .toList();

        ApiMetaResponse meta = new ApiMetaResponse(
                result.page(),
                result.size(),
                result.totalItems(),
                result.totalPages(),
                result.hasNext(),
                result.hasPrevious()
        );

        return ResponseEntity.ok(ApiResponseWrapper.success("Members fetched successfully", response, meta));
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

    @PatchMapping("/{memberId}")
    public ResponseEntity<ApiResponseWrapper<UpdateMemberResponse>> updateMember(
            @PathVariable UUID memberId,
            @Valid @RequestBody UpdateMemberRequest request) {

        UpdateMemberResult result = updateMemberUseCase.execute(
                new UpdateMemberCommand(
                        memberId,
                        request.fullName(),
                        request.email(),
                        request.address(),
                        request.nik(),
                        request.phoneNumber()
                )
        );

        UpdateMemberResponse response = new UpdateMemberResponse(
                result.id(),
                result.fullName(),
                result.email(),
                result.address(),
                result.nik(),
                result.phoneNumber(),
                result.registeredAt()
        );

        return ResponseEntity.ok(ApiResponseWrapper.success("Member updated successfully", response));
    }

    @PatchMapping("/{memberId}/kyc/approve")
    public ResponseEntity<ApiResponseWrapper<KycReviewResponse>> approveKyc(
            @PathVariable UUID memberId,
            @Valid @RequestBody ApproveKycRequest request) {

        ApproveKYCReviewResult result = approveKYCReviewUseCase.execute(
                new ApproveKYCReviewCommand(memberId, request.reviewer())
        );

        KycReviewResponse response = new KycReviewResponse(
                result.memberId(),
                result.status(),
                result.kycReviewedAt(),
                result.kycReviewedBy(),
                null
        );

        return ResponseEntity.ok(ApiResponseWrapper.success("KYC approved successfully", response));
    }

    @PatchMapping("/{memberId}/kyc/reject")
    public ResponseEntity<ApiResponseWrapper<KycReviewResponse>> rejectKyc(
            @PathVariable UUID memberId,
            @Valid @RequestBody RejectKycRequest request) {

        RejectKYCReviewResult result = rejectKYCReviewUseCase.execute(
                new RejectKYCReviewCommand(memberId, request.reviewer(), request.reviewReason())
        );

        KycReviewResponse response = new KycReviewResponse(
                result.memberId(),
                result.status(),
                result.kycReviewedAt(),
                result.kycReviewedBy(),
                result.kycReviewReason()
        );

        return ResponseEntity.ok(ApiResponseWrapper.success("KYC rejected successfully", response));
    }
}
