package com.anjasferdiansyah.koperasi.domain.repository;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsAccount;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsType;

import java.util.Optional;
import java.util.UUID;

public interface SavingsAccountRepository {

    boolean existsByMemberIdAndType(UUID memberId, SavingsType savingsType);

    Optional<SavingsAccount> findById(UUID id);

    SavingsAccount save(SavingsAccount savingsAccount);
}
