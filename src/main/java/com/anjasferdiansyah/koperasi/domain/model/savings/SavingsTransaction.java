package com.anjasferdiansyah.koperasi.domain.model.savings;

import com.anjasferdiansyah.koperasi.domain.exception.DomainValidationException;
import com.anjasferdiansyah.koperasi.domain.model.BaseDomainEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public final class SavingsTransaction extends BaseDomainEntity {

    private static final int MAX_REFERENCE_NO_LENGTH = 60;
    private static final int MAX_EXTERNAL_REFERENCE_LENGTH = 60;
    private static final int MAX_NOTE_LENGTH = 255;

    private UUID savingsAccountId;
    private UUID memberId;
    private SavingsTransactionType type;
    private BigDecimal amount;
    private String referenceNo;
    private String externalReference;
    private String note;
    private LocalDateTime occurredAt;

    private SavingsTransaction(UUID id,
                               UUID savingsAccountId,
                               UUID memberId,
                               SavingsTransactionType type,
                               BigDecimal amount,
                               String referenceNo,
                               String externalReference,
                               String note,
                               LocalDateTime occurredAt) {
        super(id);
        this.savingsAccountId = requireSavingsAccountId(savingsAccountId);
        this.memberId = requireMemberId(memberId);
        this.type = requireType(type);
        this.amount = requireAmount(amount);
        this.referenceNo = sanitizeReferenceNo(referenceNo);
        this.externalReference = sanitizeExternalReference(externalReference);
        this.note = sanitizeNote(note);
        this.occurredAt = requireOccurredAt(occurredAt);
    }

    public static SavingsTransaction create(UUID id,
                                            UUID savingsAccountId,
                                            UUID memberId,
                                            SavingsTransactionType type,
                                            BigDecimal amount,
                                            String referenceNo,
                                            String externalReference,
                                            String note,
                                            LocalDateTime occurredAt) {
        requireId(id);
        return new SavingsTransaction(
                id,
                savingsAccountId,
                memberId,
                type,
                amount,
                referenceNo,
                externalReference,
                note,
                occurredAt
        );
    }

    public static SavingsTransaction rehydrate(UUID id,
                                               UUID savingsAccountId,
                                               UUID memberId,
                                               SavingsTransactionType type,
                                               BigDecimal amount,
                                               String referenceNo,
                                               String externalReference,
                                               String note,
                                               LocalDateTime occurredAt,
                                               LocalDateTime createdAt,
                                               LocalDateTime updatedAt) {
        requireId(id);
        SavingsTransaction transaction = new SavingsTransaction(
                id,
                savingsAccountId,
                memberId,
                type,
                amount,
                referenceNo,
                externalReference,
                note,
                occurredAt
        );
        transaction.createdAt = createdAt;
        transaction.updatedAt = validateAuditTimestamps(createdAt, updatedAt);
        return transaction;
    }

    private static void requireId(UUID id) {
        if (id == null) {
            throw new DomainValidationException("Savings transaction id is required");
        }
    }

    private static UUID requireSavingsAccountId(UUID savingsAccountId) {
        if (savingsAccountId == null) {
            throw new DomainValidationException("Savings transaction account id is required");
        }
        return savingsAccountId;
    }

    private static UUID requireMemberId(UUID memberId) {
        if (memberId == null) {
            throw new DomainValidationException("Savings transaction member id is required");
        }
        return memberId;
    }

    private static SavingsTransactionType requireType(SavingsTransactionType type) {
        if (type == null) {
            throw new DomainValidationException("Savings transaction type is required");
        }
        return type;
    }

    private static BigDecimal requireAmount(BigDecimal amount) {
        Money money = Money.of(amount);
        if (!money.isPositive()) {
            throw new DomainValidationException("Savings transaction amount must be greater than zero");
        }
        return money.toBigDecimal();
    }

    private static String sanitizeReferenceNo(String referenceNo) {
        if (referenceNo == null || referenceNo.isBlank()) {
            throw new DomainValidationException("Savings transaction reference number is required");
        }
        String sanitized = referenceNo.trim();
        if (sanitized.length() > MAX_REFERENCE_NO_LENGTH) {
            throw new DomainValidationException("Savings transaction reference number is too long");
        }
        return sanitized;
    }

    private static String sanitizeNote(String note) {
        if (note == null || note.isBlank()) {
            return null;
        }
        String sanitized = note.trim();
        if (sanitized.length() > MAX_NOTE_LENGTH) {
            throw new DomainValidationException("Savings transaction note is too long");
        }
        return sanitized;
    }

    private static String sanitizeExternalReference(String externalReference) {
        if (externalReference == null || externalReference.isBlank()) {
            return null;
        }
        String sanitized = externalReference.trim();
        if (sanitized.length() > MAX_EXTERNAL_REFERENCE_LENGTH) {
            throw new DomainValidationException("Savings transaction external reference is too long");
        }
        return sanitized;
    }

    private static LocalDateTime requireOccurredAt(LocalDateTime occurredAt) {
        if (occurredAt == null) {
            throw new DomainValidationException("Savings transaction occurred at is required");
        }
        return occurredAt;
    }

    private static LocalDateTime validateAuditTimestamps(LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (createdAt == null) {
            return updatedAt;
        }
        if (updatedAt != null && updatedAt.isBefore(createdAt)) {
            throw new DomainValidationException("Savings transaction updatedAt cannot be before createdAt");
        }
        return updatedAt;
    }
}
