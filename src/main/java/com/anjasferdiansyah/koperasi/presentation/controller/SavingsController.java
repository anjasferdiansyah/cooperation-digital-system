package com.anjasferdiansyah.koperasi.presentation.controller;

import com.anjasferdiansyah.koperasi.application.usecase.savings.open_account.OpenSavingsAccountCommand;
import com.anjasferdiansyah.koperasi.application.usecase.savings.open_account.OpenSavingsAccountResult;
import com.anjasferdiansyah.koperasi.application.usecase.savings.open_account.OpenSavingsAccountUseCase;
import com.anjasferdiansyah.koperasi.presentation.response.ApiResponseWrapper;
import com.anjasferdiansyah.koperasi.presentation.response.OpenSavingsAccountRequest;
import com.anjasferdiansyah.koperasi.presentation.response.OpenSavingsAccountResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/savings/accounts")
public class SavingsController {

    private final OpenSavingsAccountUseCase openSavingsAccountUseCase;

    public SavingsController(OpenSavingsAccountUseCase openSavingsAccountUseCase) {
        this.openSavingsAccountUseCase = openSavingsAccountUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponseWrapper<OpenSavingsAccountResponse>> openAccount(
            @Valid @RequestBody OpenSavingsAccountRequest request) {
        OpenSavingsAccountResult result = openSavingsAccountUseCase.execute(
                new OpenSavingsAccountCommand(
                        request.memberId(),
                        request.savingsType()
                )
        );

        OpenSavingsAccountResponse response = new OpenSavingsAccountResponse(
                result.id(),
                result.memberId(),
                result.accountNo(),
                result.savingsType(),
                result.status(),
                result.balance(),
                result.openedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.success("Savings account opened successfully", response));
    }
}
