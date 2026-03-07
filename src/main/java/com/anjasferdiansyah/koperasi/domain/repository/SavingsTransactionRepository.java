package com.anjasferdiansyah.koperasi.domain.repository;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsTransaction;

public interface SavingsTransactionRepository {

    SavingsTransaction save(SavingsTransaction transaction);
}
