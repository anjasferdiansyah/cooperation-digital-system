package com.anjasferdiansyah.koperasi.domain.repository;

import com.anjasferdiansyah.koperasi.domain.model.member.Member;
import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {

    boolean existsByEmail(String email);
    boolean existsByNik(String nik);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Member> findById(UUID id);
    PageResult<Member> findAll(int page, int size, String sortBy, String sortDir);
    PageResult<Member> search(String keyword, int page, int size, String sortBy, String sortDir);

    Member save(Member member);
}
