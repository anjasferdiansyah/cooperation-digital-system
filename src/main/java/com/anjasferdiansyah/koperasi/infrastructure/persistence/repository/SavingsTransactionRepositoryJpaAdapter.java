package com.anjasferdiansyah.koperasi.infrastructure.persistence.repository;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsTransaction;
import com.anjasferdiansyah.koperasi.domain.repository.SavingsTransactionRepository;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.entity.SavingsTransactionJpaEntity;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.mapper.SavingsTransactionPersistenceMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SavingsTransactionRepositoryJpaAdapter implements SavingsTransactionRepository {

    private final SavingsTransactionJpaRepository savingsTransactionJpaRepository;
    private final SavingsTransactionPersistenceMapper mapper;

    public SavingsTransactionRepositoryJpaAdapter(SavingsTransactionJpaRepository savingsTransactionJpaRepository,
                                                  SavingsTransactionPersistenceMapper mapper) {
        this.savingsTransactionJpaRepository = savingsTransactionJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public SavingsTransaction save(SavingsTransaction transaction) {
        SavingsTransactionJpaEntity saved = savingsTransactionJpaRepository.save(mapper.toJpaEntity(transaction));
        return mapper.toDomain(saved);
    }
}
