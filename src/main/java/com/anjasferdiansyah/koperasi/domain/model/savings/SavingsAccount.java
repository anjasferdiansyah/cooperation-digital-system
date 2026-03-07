package com.anjasferdiansyah.koperasi.domain.model.savings;

import com.anjasferdiansyah.koperasi.domain.exception.DomainValidationException;
import com.anjasferdiansyah.koperasi.domain.model.BaseDomainEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public final class SavingsAccount extends BaseDomainEntity {

    private static final int MAX_ACCOUNT_NO_LENGTH = 40;

    private UUID memberId;
    private String accountNo;
    private SavingsType type;
    private SavingsAccountStatus status;
    private BigDecimal balance;
    private Long version;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    private SavingsAccount(UUID id,
                           UUID memberId,
                           String accountNo,
                           SavingsType type,
                           LocalDateTime openedAt) {
        super(id);
        this.memberId = requireMemberId(memberId);
        this.accountNo = sanitizeAccountNo(accountNo);
        this.type = requireType(type);
        this.status = SavingsAccountStatus.ACTIVE;
        this.balance = Money.of(BigDecimal.ZERO).toBigDecimal();
        this.version = 0L;
        this.openedAt = requireOpenedAt(openedAt);
    }

    public static SavingsAccount open(UUID id,
                                      UUID memberId,
                                      String accountNo,
                                      SavingsType type,
                                      LocalDateTime openedAt) {
        requireId(id);
        return new SavingsAccount(id, memberId, accountNo, type, openedAt);
    }

    public static SavingsAccount rehydrate(UUID id,
                                           UUID memberId,
                                           String accountNo,
                                           SavingsType type,
                                           SavingsAccountStatus status,
                                           BigDecimal balance,
                                           Long version,
                                           LocalDateTime openedAt,
                                           LocalDateTime closedAt,
                                           LocalDateTime createdAt,
                                           LocalDateTime updatedAt) {
        requireId(id);
        SavingsAccount account = new SavingsAccount(id, memberId, accountNo, type, openedAt);
        account.status = status == null ? SavingsAccountStatus.ACTIVE : status;
        account.balance = requireBalance(balance);
        account.version = requireVersion(version);
        account.closedAt = closedAt;
        validateState(account.status, account.openedAt, account.closedAt);
        account.createdAt = createdAt;
        account.updatedAt = validateAuditTimestamps(createdAt, updatedAt);
        return account;
    }

    public SavingsTransaction deposit(BigDecimal amount,
                                      String referenceNo,
                                      String externalReference,
                                      String note,
                                      LocalDateTime occurredAt) {
        ensureActive();
        Money depositAmount = positiveAmount(amount, "Deposit amount must be greater than zero");
        this.balance = Money.of(this.balance).add(depositAmount).toBigDecimal();

        return SavingsTransaction.create(
                UUID.randomUUID(),
                this.id,
                this.memberId,
                SavingsTransactionType.DEPOSIT,
                depositAmount.toBigDecimal(),
                referenceNo,
                externalReference,
                note,
                occurredAt
        );
    }

    public SavingsTransaction withdraw(BigDecimal amount,
                                       String referenceNo,
                                       String externalReference,
                                       String note,
                                       LocalDateTime occurredAt) {
        ensureActive();

        if(!this.type.isWithdrawAllowed()){
            throw new DomainValidationException("Withdrawal is not allowed for savings type " + this.type);
        }

        Money withdrawalAmount = positiveAmount(amount, "Withdrawal amount must be greater than zero");
        Money currentBalance = Money.of(this.balance);
        if (currentBalance.isLessThan(withdrawalAmount)) {
            throw new DomainValidationException("Insufficient savings balance");
        }

        this.balance = currentBalance.subtract(withdrawalAmount).toBigDecimal();

        return SavingsTransaction.create(
                UUID.randomUUID(),
                this.id,
                this.memberId,
                SavingsTransactionType.WITHDRAWAL,
                withdrawalAmount.toBigDecimal(),
                referenceNo,
                externalReference,
                note,
                occurredAt
        );
    }

    public void close(LocalDateTime closedAt) {
        ensureActive();
        if (Money.of(this.balance).toBigDecimal().signum() > 0) {
            throw new DomainValidationException("Savings account with remaining balance cannot be closed");
        }
        if (closedAt == null) {
            throw new DomainValidationException("Savings account closed at is required");
        }
        if (closedAt.isBefore(this.openedAt)) {
            throw new DomainValidationException("Savings account closed at cannot be before opened at");
        }
        this.status = SavingsAccountStatus.CLOSED;
        this.closedAt = closedAt;
    }

    private void ensureActive() {
        if (this.status != SavingsAccountStatus.ACTIVE) {
            throw new DomainValidationException("Savings account is not active");
        }
    }

    private static Money positiveAmount(BigDecimal amount, String message) {
        Money money = Money.of(amount);
        if (!money.isPositive()) {
            throw new DomainValidationException(message);
        }
        return money;
    }

    private static void requireId(UUID id) {
        if (id == null) {
            throw new DomainValidationException("Savings account id is required");
        }
    }

    private static UUID requireMemberId(UUID memberId) {
        if (memberId == null) {
            throw new DomainValidationException("Savings account member id is required");
        }
        return memberId;
    }

    private static String sanitizeAccountNo(String accountNo) {
        if (accountNo == null || accountNo.isBlank()) {
            throw new DomainValidationException("Savings account number is required");
        }
        String sanitized = accountNo.trim();
        if (sanitized.length() > MAX_ACCOUNT_NO_LENGTH) {
            throw new DomainValidationException("Savings account number is too long");
        }
        return sanitized;
    }

    private static SavingsType requireType(SavingsType type) {
        if (type == null) {
            throw new DomainValidationException("Savings account type is required");
        }
        return type;
    }

    private static BigDecimal requireBalance(BigDecimal balance) {
        Money money = Money.of(balance);
        if (money.isNegative()) {
            throw new DomainValidationException("Savings account balance cannot be negative");
        }
        return money.toBigDecimal();
    }

    private static Long requireVersion(Long version) {
        if (version == null) {
            throw new DomainValidationException("Savings account version is required");
        }
        if (version < 0) {
            throw new DomainValidationException("Savings account version cannot be negative");
        }
        return version;
    }

    private static void validateState(SavingsAccountStatus status, LocalDateTime openedAt, LocalDateTime closedAt) {
        if (status == SavingsAccountStatus.CLOSED) {
            if (closedAt == null) {
                throw new DomainValidationException("Closed savings account must have closed at");
            }
            if (closedAt.isBefore(openedAt)) {
                throw new DomainValidationException("Savings account closed at cannot be before opened at");
            }
        }
        if (status == SavingsAccountStatus.ACTIVE && closedAt != null) {
            throw new DomainValidationException("Active savings account cannot have closed at");
        }
    }

    private static LocalDateTime requireOpenedAt(LocalDateTime openedAt) {
        if (openedAt == null) {
            throw new DomainValidationException("Savings account opened at is required");
        }
        return openedAt;
    }

    private static LocalDateTime validateAuditTimestamps(LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (createdAt == null) {
            return updatedAt;
        }
        if (updatedAt != null && updatedAt.isBefore(createdAt)) {
            throw new DomainValidationException("Savings account updatedAt cannot be before createdAt");
        }
        return updatedAt;
    }
}
