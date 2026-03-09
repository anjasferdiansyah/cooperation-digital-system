package com.anjasferdiansyah.koperasi.application.usecase.savings.deposit;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DepositSavingsResult(
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
