package com.anjasferdiansyah.koperasi.infrastructure.persistence.repository;

import com.anjasferdiansyah.koperasi.infrastructure.persistence.entity.MemberJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, UUID> {

    boolean existsByEmail(String email);
    boolean existsByNik(String nik);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("""
            SELECT m
            FROM MemberJpaEntity m
            WHERE LOWER(m.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR m.nik LIKE CONCAT('%', :keyword, '%')
               OR m.phoneNumber LIKE CONCAT('%', :keyword, '%')
            """)
    Page<MemberJpaEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
