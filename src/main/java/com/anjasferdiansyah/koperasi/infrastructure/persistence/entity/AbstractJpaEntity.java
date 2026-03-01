package com.anjasferdiansyah.koperasi.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Abstract JPA base entity for persistence concerns.
 * <p>
 * This class centralizes technical identity and audit timestamp handling in the infrastructure layer.
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AbstractJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void prePersist() {
        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        if (createdAt == null) {
            createdAt = nowUtc;
        }
        updatedAt = nowUtc;
    }

    @PreUpdate
    protected void preUpdate() {
        updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }
}
