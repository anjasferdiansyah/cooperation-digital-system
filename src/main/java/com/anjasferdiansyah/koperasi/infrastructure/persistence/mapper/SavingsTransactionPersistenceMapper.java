package com.anjasferdiansyah.koperasi.infrastructure.persistence.mapper;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsTransaction;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.entity.SavingsTransactionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SavingsTransactionPersistenceMapper {

    public SavingsTransactionJpaEntity toJpaEntity(SavingsTransaction transaction) {
        SavingsTransactionJpaEntity entity = new SavingsTransactionJpaEntity();
        entity.assignIdentity(transaction.getId());
        entity.setSavingsAccountId(transaction.getSavingsAccountId());
        entity.setMemberId(transaction.getMemberId());
        entity.setTransactionType(transaction.getType());
        entity.setAmount(transaction.getAmount());
        entity.setReferenceNo(transaction.getReferenceNo());
        entity.setExternalReference(transaction.getExternalReference());
        entity.setNote(transaction.getNote());
        entity.setOccurredAt(transaction.getOccurredAt());
        return entity;
    }

    public SavingsTransaction toDomain(SavingsTransactionJpaEntity entity) {
        return SavingsTransaction.rehydrate(
                entity.getId(),
                entity.getSavingsAccountId(),
                entity.getMemberId(),
                entity.getTransactionType(),
                entity.getAmount(),
                entity.getReferenceNo(),
                entity.getExternalReference(),
                entity.getNote(),
                entity.getOccurredAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
