package com.anjasferdiansyah.koperasi.infrastructure.persistence.entity;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsTransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Table(name = "savings_transactions")
public class SavingsTransactionJpaEntity extends AbstractJpaEntity {

    @Column(name = "savings_account_id", nullable = false)
    private UUID savingsAccountId;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private SavingsTransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "reference_no", nullable = false, unique = true, length = 60)
    private String referenceNo;

    @Column(name = "external_reference", length = 60)
    private String externalReference;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    public void assignIdentity(UUID id) {
        setId(id);
    }
}
