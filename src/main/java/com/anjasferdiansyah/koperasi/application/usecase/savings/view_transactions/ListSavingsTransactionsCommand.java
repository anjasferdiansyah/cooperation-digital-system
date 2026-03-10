package com.anjasferdiansyah.koperasi.application.usecase.savings.view_transactions;

import java.util.UUID;

public record ListSavingsTransactionsCommand(
        UUID accountId,
        int page,
        int size,
        String sortBy,
        String sortDir
) {
}
