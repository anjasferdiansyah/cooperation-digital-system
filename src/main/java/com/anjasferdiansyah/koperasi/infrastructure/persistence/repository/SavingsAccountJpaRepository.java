package com.anjasferdiansyah.koperasi.infrastructure.persistence.repository;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsType;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.entity.SavingsAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SavingsAccountJpaRepository extends JpaRepository<SavingsAccountJpaEntity, UUID> {

    boolean existsByMemberIdAndSavingsType(UUID memberId, SavingsType savingsType);
}
