package com.anjasferdiansyah.koperasi.application.usecase.savings.withdraw;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WithdrawSavingsCommand(
        UUID accountId,
        BigDecimal amount,
        String externalReference,
        String note,
        LocalDateTime occurredAt
) {
}
