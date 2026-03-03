package com.anjasferdiansyah.koperasi.application.usecase.savings.open_account;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsType;

import java.util.UUID;

public record OpenSavingsAccountCommand(
        UUID memberId,
        SavingsType savingsType
) {
}
