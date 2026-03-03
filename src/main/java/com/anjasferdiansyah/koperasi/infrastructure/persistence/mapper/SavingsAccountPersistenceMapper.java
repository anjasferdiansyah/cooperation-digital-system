package com.anjasferdiansyah.koperasi.infrastructure.persistence.mapper;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsAccount;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.entity.SavingsAccountJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SavingsAccountPersistenceMapper {

    public SavingsAccountJpaEntity toJpaEntity(SavingsAccount savingsAccount) {
        SavingsAccountJpaEntity entity = new SavingsAccountJpaEntity();
        entity.assignIdentity(savingsAccount.getId());
        entity.setMemberId(savingsAccount.getMemberId());
        entity.setAccountNo(savingsAccount.getAccountNo());
        entity.setSavingsType(savingsAccount.getType());
        entity.setStatus(savingsAccount.getStatus());
        entity.setBalance(savingsAccount.getBalance());
        if (savingsAccount.getCreatedAt() != null) {
            entity.setVersion(savingsAccount.getVersion());
        }
        entity.setOpenedAt(savingsAccount.getOpenedAt());
        entity.setClosedAt(savingsAccount.getClosedAt());
        return entity;
    }

    public SavingsAccount toDomain(SavingsAccountJpaEntity entity) {
        return SavingsAccount.rehydrate(
                entity.getId(),
                entity.getMemberId(),
                entity.getAccountNo(),
                entity.getSavingsType(),
                entity.getStatus(),
                entity.getBalance(),
                entity.getVersion(),
                entity.getOpenedAt(),
                entity.getClosedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
