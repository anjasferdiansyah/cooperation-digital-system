package com.anjasferdiansyah.koperasi.infrastructure.persistence.repository;

import com.anjasferdiansyah.koperasi.infrastructure.persistence.entity.SavingsTransactionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SavingsTransactionJpaRepository extends JpaRepository<SavingsTransactionJpaEntity, UUID> {

    Page<SavingsTransactionJpaEntity> findBySavingsAccountId(UUID savingsAccountId, Pageable pageable);
}
