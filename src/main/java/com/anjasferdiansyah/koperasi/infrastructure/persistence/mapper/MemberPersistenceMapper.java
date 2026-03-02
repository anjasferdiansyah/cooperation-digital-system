package com.anjasferdiansyah.koperasi.infrastructure.persistence.mapper;

import com.anjasferdiansyah.koperasi.domain.model.Member;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.entity.MemberJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberPersistenceMapper {

    public MemberJpaEntity toJpaEntity(Member member) {
        MemberJpaEntity jpaEntity = new MemberJpaEntity();
        jpaEntity.assignIdentity(member.getId());
        jpaEntity.setFullName(member.getFullName());
        jpaEntity.setEmail(member.getEmail());
        jpaEntity.setAddress(member.getAddress());
        jpaEntity.setNik(member.getNik());
        jpaEntity.setPhoneNumber(member.getPhoneNumber());
        jpaEntity.setStatus(member.getStatus());
        jpaEntity.setKycReviewedAt(member.getKycReviewedAt());
        jpaEntity.setKycReviewedBy(member.getKycReviewedBy());
        jpaEntity.setRegisteredAt(member.getRegisteredAt());
        return jpaEntity;
    }

    public Member toDomain(MemberJpaEntity entity) {
        return Member.rehydrate(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getAddress(),
                entity.getNik(),
                entity.getPhoneNumber(),
                entity.getStatus(),
                entity.getKycReviewedAt(),
                entity.getKycReviewedBy(),
                entity.getRegisteredAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
