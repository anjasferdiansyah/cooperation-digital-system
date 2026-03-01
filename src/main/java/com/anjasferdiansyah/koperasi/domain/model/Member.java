package com.anjasferdiansyah.koperasi.domain.model;

import com.anjasferdiansyah.koperasi.domain.exception.DomainValidationException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Getter
public final class Member extends BaseDomainEntity {

    private static final int MAX_FULL_NAME_LENGTH = 150;
    private static final int MAX_EMAIL_LENGTH = 150;
    private static final int MAX_ADDRESS_LENGTH = 255;
    private static final int MAX_PHONE_NUMBER_LENGTH = 20;
    private static final Pattern NIK_PATTERN = Pattern.compile("^\\d{16}$");
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\+?[0-9]{9,20}$");
    private static final Pattern SIMPLE_EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String fullName;
    private final String email;
    private final String address;
    private final String nik;
    private final String phoneNumber;
    private final LocalDateTime registeredAt;

    private Member(UUID id,
                   String fullName,
                   String email,
                   String address,
                   String nik,
                   String phoneNumber,
                   LocalDateTime registeredAt) {
        super(id);
        this.fullName = sanitizeAndValidateFullName(fullName);
        this.email = sanitizeAndValidateEmail(email);
        this.address = sanitizeAndValidateAddress(address);
        this.nik = sanitizeAndValidateNik(nik);
        this.phoneNumber = sanitizeAndValidatePhoneNumber(phoneNumber);
        this.registeredAt = requireRegisteredAt(registeredAt);
    }

    public static Member register(UUID id,
                                  String fullName,
                                  String email,
                                  String address,
                                  String nik,
                                  String phoneNumber,
                                  LocalDateTime registeredAt) {
        requireId(id);
        return new Member(id, fullName, email, address, nik, phoneNumber, registeredAt);
    }

    public static Member rehydrate(UUID id,
                                   String fullName,
                                   String email,
                                   String address,
                                   String nik,
                                   String phoneNumber,
                                   LocalDateTime registeredAt,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt) {
        requireId(id);
        Member member = new Member(id, fullName, email, address, nik, phoneNumber, registeredAt);
        member.createdAt = createdAt;
        member.updatedAt = validateAuditTimestamps(createdAt, updatedAt);
        return member;
    }

    private static void requireId(UUID id) {
        if (id == null) {
            throw new DomainValidationException("Member id is required");
        }
    }

    private static String sanitizeAndValidateFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new DomainValidationException("Member full name is required");
        }
        String sanitized = fullName.trim();
        if (sanitized.length() > MAX_FULL_NAME_LENGTH) {
            throw new DomainValidationException("Member full name is too long");
        }
        return sanitized;
    }

    private static String sanitizeAndValidateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new DomainValidationException("Member email is required");
        }

        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() > MAX_EMAIL_LENGTH) {
            throw new DomainValidationException("Member email is too long");
        }
        if (!SIMPLE_EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new DomainValidationException("Member email format is invalid");
        }
        return normalized;
    }

    private static String sanitizeAndValidateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new DomainValidationException("Member address is required");
        }
        String sanitized = address.trim();
        if (sanitized.length() > MAX_ADDRESS_LENGTH) {
            throw new DomainValidationException("Member address is too long");
        }
        return sanitized;
    }

    private static String sanitizeAndValidateNik(String nik) {
        if (nik == null || nik.isBlank()) {
            throw new DomainValidationException("Member nik is required");
        }
        String sanitized = nik.trim();
        if (!NIK_PATTERN.matcher(sanitized).matches()) {
            throw new DomainValidationException("Member nik must be 16 digits");
        }
        return sanitized;
    }

    private static String sanitizeAndValidatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new DomainValidationException("Member phone number is required");
        }
        String sanitized = phoneNumber.trim();
        if (sanitized.length() > MAX_PHONE_NUMBER_LENGTH) {
            throw new DomainValidationException("Member phone number is too long");
        }
        if (!PHONE_NUMBER_PATTERN.matcher(sanitized).matches()) {
            throw new DomainValidationException("Member phone number format is invalid");
        }
        return sanitized;
    }

    private static LocalDateTime requireRegisteredAt(LocalDateTime registeredAt) {
        if (registeredAt == null) {
            throw new DomainValidationException("Member registration time is required");
        }
        return registeredAt;
    }

    private static LocalDateTime validateAuditTimestamps(LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (createdAt == null) {
            return updatedAt;
        }
        if (updatedAt != null && updatedAt.isBefore(createdAt)) {
            throw new DomainValidationException("Member updatedAt cannot be before createdAt");
        }
        return updatedAt;
    }
}
