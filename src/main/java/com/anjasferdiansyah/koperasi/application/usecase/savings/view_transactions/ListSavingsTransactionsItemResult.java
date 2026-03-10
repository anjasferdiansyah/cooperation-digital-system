package com.anjasferdiansyah.koperasi.application.usecase.savings.view_transactions;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ListSavingsTransactionsItemResult(
        UUID transactionId,
        UUID accountId,
        UUID memberId,
        SavingsTransactionType type,
        BigDecimal amount,
        String referenceNo,
        String externalReference,
        String note,
        LocalDateTime occurredAt
) {
}
