package com.anjasferdiansyah.koperasi.application.usecase.savings.deposit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DepositSavingsCommand(
        UUID accountId,
        BigDecimal amount,
        String externalReference,
        String note,
        LocalDateTime occurredAt
) {
}
