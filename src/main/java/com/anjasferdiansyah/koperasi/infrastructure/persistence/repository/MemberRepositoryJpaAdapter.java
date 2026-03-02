package com.anjasferdiansyah.koperasi.infrastructure.persistence.repository;

import com.anjasferdiansyah.koperasi.domain.model.Member;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.entity.MemberJpaEntity;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.mapper.MemberPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class MemberRepositoryJpaAdapter implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberPersistenceMapper mapper;

    public MemberRepositoryJpaAdapter(MemberJpaRepository memberJpaRepository, MemberPersistenceMapper mapper) {
        this.memberJpaRepository = memberJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNik(String nik) {
        return memberJpaRepository.existsByNik(nik);
    }

    @Override
    public Optional<Member> findById(UUID id) {
        return memberJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Member save(Member member) {
        MemberJpaEntity saved = memberJpaRepository.save(mapper.toJpaEntity(member));
        return mapper.toDomain(saved);
    }
}
