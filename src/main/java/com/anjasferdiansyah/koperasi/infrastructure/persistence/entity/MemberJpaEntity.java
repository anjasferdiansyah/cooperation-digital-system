package com.anjasferdiansyah.koperasi.infrastructure.persistence.entity;

import com.anjasferdiansyah.koperasi.domain.model.member.MemberStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Table(name = "members")
public class MemberJpaEntity extends AbstractJpaEntity {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "nik", nullable = false, unique = true, length = 16)
    private String nik;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MemberStatus status;

    @Column(name = "kyc_reviewed_at")
    private LocalDateTime kycReviewedAt;

    @Column(name = "kyc_reviewed_by", length = 120)
    private String kycReviewedBy;

    @Column(name = "kyc_review_reason", length = 500)
    private String kycReviewReason;

    @Column(name = "rejected_by", length = 120)
    private String rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    public void assignIdentity(java.util.UUID id) {
        setId(id);
    }
}
