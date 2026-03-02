package com.anjasferdiansyah.koperasi.infrastructure.persistence.repository;

import com.anjasferdiansyah.koperasi.domain.model.member.Member;
import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.entity.MemberJpaEntity;
import com.anjasferdiansyah.koperasi.infrastructure.persistence.mapper.MemberPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public boolean existsByPhoneNumber(String phoneNumber) {
        return memberJpaRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<Member> findById(UUID id) {
        return memberJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<Member> findAll(int page, int size, String sortBy, String sortDir) {
        PageRequest pageRequest = PageRequest.of(page, size, buildSort(sortBy, sortDir));
        Page<MemberJpaEntity> result = memberJpaRepository.findAll(pageRequest);
        return toPageResult(result);
    }

    @Override
    public PageResult<Member> search(String keyword, int page, int size, String sortBy, String sortDir) {
        PageRequest pageRequest = PageRequest.of(page, size, buildSort(sortBy, sortDir));
        if (keyword == null || keyword.isBlank()) {
            Page<MemberJpaEntity> result = memberJpaRepository.findAll(pageRequest);
            return toPageResult(result);
        }

        Page<MemberJpaEntity> result = memberJpaRepository.searchByKeyword(keyword.trim(), pageRequest);
        return toPageResult(result);
    }

    @Override
    public Member save(Member member) {
        MemberJpaEntity saved = memberJpaRepository.save(mapper.toJpaEntity(member));
        return mapper.toDomain(saved);
    }

    private PageResult<Member> toPageResult(Page<MemberJpaEntity> result) {
        return new PageResult<>(
                result.getContent().stream().map(mapper::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext(),
                result.hasPrevious()
        );
    }

    private Sort buildSort(String sortBy, String sortDir) {
        if ("asc".equalsIgnoreCase(sortDir)) {
            return Sort.by(sortBy).ascending();
        }
        return Sort.by(sortBy).descending();
    }
}
