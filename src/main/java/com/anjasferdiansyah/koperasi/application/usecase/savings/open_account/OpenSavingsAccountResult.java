package com.anjasferdiansyah.koperasi.application.usecase.savings.open_account;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsAccountStatus;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OpenSavingsAccountResult(
        UUID id,
        UUID memberId,
        String accountNo,
        SavingsType savingsType,
        SavingsAccountStatus status,
        BigDecimal balance,
        LocalDateTime openedAt
) {
}
