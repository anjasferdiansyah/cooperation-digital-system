package com.anjasferdiansyah.koperasi.application.usecase.savings.withdraw;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WithdrawSavingsResult(
        UUID transactionId,
        UUID accountId,
        UUID memberId,
        SavingsTransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String referenceNo,
        String externalReference,
        String note,
        LocalDateTime occurredAt
) {
}
