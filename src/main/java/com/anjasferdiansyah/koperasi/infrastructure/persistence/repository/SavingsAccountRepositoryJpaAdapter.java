package com.anjasferdiansyah.koperasi.infrastructure.persistence.repository;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsAccount;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsType;
import com.anjasferdiansyah.koperasi.domain.repository.SavingsAccountRepository;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.entity.SavingsAccountJpaEntity;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.mapper.SavingsAccountPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SavingsAccountRepositoryJpaAdapter implements SavingsAccountRepository {

    private final SavingsAccountJpaRepository savingsAccountJpaRepository;
    private final SavingsAccountPersistenceMapper mapper;

    public SavingsAccountRepositoryJpaAdapter(SavingsAccountJpaRepository savingsAccountJpaRepository,
                                              SavingsAccountPersistenceMapper mapper) {
        this.savingsAccountJpaRepository = savingsAccountJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public boolean existsByMemberIdAndType(UUID memberId, SavingsType savingsType) {
        return savingsAccountJpaRepository.existsByMemberIdAndSavingsType(memberId, savingsType);
    }

    @Override
    public Optional<SavingsAccount> findById(UUID id) {
        return savingsAccountJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public SavingsAccount save(SavingsAccount savingsAccount) {
        SavingsAccountJpaEntity saved = savingsAccountJpaRepository.save(mapper.toJpaEntity(savingsAccount));
        return mapper.toDomain(saved);
    }
}
