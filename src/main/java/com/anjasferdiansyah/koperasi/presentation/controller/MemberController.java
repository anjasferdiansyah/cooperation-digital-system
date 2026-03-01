package com.anjasferdiansyah.koperasi.presentation.controller;

import com.anjasferdiansyah.koperasi.application.usecase.member.create.CreateMemberCommand;
import com.anjasferdiansyah.koperasi.application.usecase.member.create.CreateMemberResult;
import com.anjasferdiansyah.koperasi.application.usecase.member.create.CreateMemberUseCase;
import com.anjasferdiansyah.koperasi.presentation.response.ApiResponseWrapper;
import com.anjasferdiansyah.koperasi.presentation.response.CreateMemberRequest;
import com.anjasferdiansyah.koperasi.presentation.response.CreateMemberResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final CreateMemberUseCase createMemberUseCase;

    public MemberController(CreateMemberUseCase createMemberUseCase) {
        this.createMemberUseCase = createMemberUseCase;
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
}
