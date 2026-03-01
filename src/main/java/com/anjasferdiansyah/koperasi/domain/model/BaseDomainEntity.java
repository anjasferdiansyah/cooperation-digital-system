package com.anjasferdiansyah.koperasi.domain.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract base domain entity for core business models.
 * Framework independent.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseDomainEntity {

    @EqualsAndHashCode.Include
    protected UUID id;

    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    protected BaseDomainEntity(UUID id) {
        this.id = id;
    }
}