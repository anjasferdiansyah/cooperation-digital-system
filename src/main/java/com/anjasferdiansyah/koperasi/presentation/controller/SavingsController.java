package com.anjasferdiansyah.koperasi.presentation.controller;

import com.anjasferdiansyah.koperasi.application.usecase.savings.deposit.DepositSavingsCommand;
import com.anjasferdiansyah.koperasi.application.usecase.savings.deposit.DepositSavingsResult;
import com.anjasferdiansyah.koperasi.application.usecase.savings.deposit.DepositSavingsUseCase;
import com.anjasferdiansyah.koperasi.application.usecase.savings.open_account.OpenSavingsAccountCommand;
import com.anjasferdiansyah.koperasi.application.usecase.savings.open_account.OpenSavingsAccountResult;
import com.anjasferdiansyah.koperasi.application.usecase.savings.open_account.OpenSavingsAccountUseCase;
import com.anjasferdiansyah.koperasi.application.usecase.savings.withdraw.WithdrawSavingsCommand;
import com.anjasferdiansyah.koperasi.application.usecase.savings.withdraw.WithdrawSavingsResult;
import com.anjasferdiansyah.koperasi.application.usecase.savings.withdraw.WithdrawSavingsUseCase;
import com.anjasferdiansyah.koperasi.presentation.response.ApiResponseWrapper;
import com.anjasferdiansyah.koperasi.presentation.response.CreateSavingsTransactionRequest;
import com.anjasferdiansyah.koperasi.presentation.response.OpenSavingsAccountRequest;
import com.anjasferdiansyah.koperasi.presentation.response.OpenSavingsAccountResponse;
import com.anjasferdiansyah.koperasi.presentation.response.SavingsTransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/savings/accounts")
public class SavingsController {

    private final OpenSavingsAccountUseCase openSavingsAccountUseCase;
    private final DepositSavingsUseCase depositSavingsUseCase;
    private final WithdrawSavingsUseCase withdrawSavingsUseCase;

    public SavingsController(OpenSavingsAccountUseCase openSavingsAccountUseCase,
                             DepositSavingsUseCase depositSavingsUseCase,
                             WithdrawSavingsUseCase withdrawSavingsUseCase) {
        this.openSavingsAccountUseCase = openSavingsAccountUseCase;
        this.depositSavingsUseCase = depositSavingsUseCase;
        this.withdrawSavingsUseCase = withdrawSavingsUseCase;
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

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<ApiResponseWrapper<SavingsTransactionResponse>> deposit(
            @PathVariable UUID accountId,
            @Valid @RequestBody CreateSavingsTransactionRequest request) {
        DepositSavingsResult result = depositSavingsUseCase.execute(
                new DepositSavingsCommand(
                        accountId,
                        request.amount(),
                        request.externalReference(),
                        request.note(),
                        request.occurredAt()
                )
        );

        SavingsTransactionResponse response = new SavingsTransactionResponse(
                result.transactionId(),
                result.accountId(),
                result.memberId(),
                result.type(),
                result.amount(),
                result.balanceAfter(),
                result.referenceNo(),
                result.externalReference(),
                result.note(),
                result.occurredAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.success("Savings deposit recorded successfully", response));
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<ApiResponseWrapper<SavingsTransactionResponse>> withdraw(
            @PathVariable UUID accountId,
            @Valid @RequestBody CreateSavingsTransactionRequest request) {
        WithdrawSavingsResult result = withdrawSavingsUseCase.execute(
                new WithdrawSavingsCommand(
                        accountId,
                        request.amount(),
                        request.externalReference(),
                        request.note(),
                        request.occurredAt()
                )
        );

        SavingsTransactionResponse response = new SavingsTransactionResponse(
                result.transactionId(),
                result.accountId(),
                result.memberId(),
                result.type(),
                result.amount(),
                result.balanceAfter(),
                result.referenceNo(),
                result.externalReference(),
                result.note(),
                result.occurredAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.success("Savings withdrawal recorded successfully", response));
    }
}
