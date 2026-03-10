package com.anjasferdiansyah.koperasi.domain.repository;

import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsTransaction;

import java.util.UUID;

public interface SavingsTransactionRepository {

    SavingsTransaction save(SavingsTransaction transaction);

    PageResult<SavingsTransaction> findBySavingsAccountId(UUID accountId, int page, int size, String sortBy, String sortDir);
}
