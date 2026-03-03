package com.anjasferdiansyah.koperasi.infrastructure.persistence.entity;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsAccountStatus;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
@Table(name = "savings_accounts")
public class SavingsAccountJpaEntity extends AbstractJpaEntity {

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "account_no", nullable = false, unique = true, length = 40)
    private String accountNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "savings_type", nullable = false, length = 20)
    private SavingsType savingsType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SavingsAccountStatus status;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    public void assignIdentity(UUID id) {
        setId(id);
    }
}
