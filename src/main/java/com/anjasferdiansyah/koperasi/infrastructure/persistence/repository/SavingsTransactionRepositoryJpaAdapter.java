package com.anjasferdiansyah.koperasi.infrastructure.persistence.repository;

import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsTransaction;
import com.anjasferdiansyah.koperasi.domain.repository.SavingsTransactionRepository;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.entity.SavingsTransactionJpaEntity;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.mapper.SavingsTransactionPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.UUID;

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

    @Override
    public PageResult<SavingsTransaction> findBySavingsAccountId(UUID accountId, int page, int size, String sortBy, String sortDir) {
        PageRequest pageRequest = PageRequest.of(page, size, buildSort(sortBy, sortDir));
        Page<SavingsTransactionJpaEntity> result = savingsTransactionJpaRepository.findBySavingsAccountId(accountId, pageRequest);
        return new PageResult<>(
                result.getContent().stream().map(mapper::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext(),
                result.hasPrevious()
        );
    }

    private Sort buildSort(String sortBy, String sortDir) {
        if ("asc".equalsIgnoreCase(sortDir)) {
            return Sort.by(sortBy).ascending();
        }
        return Sort.by(sortBy).descending();
    }
}
