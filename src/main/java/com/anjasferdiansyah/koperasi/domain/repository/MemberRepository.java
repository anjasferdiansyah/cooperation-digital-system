package com.anjasferdiansyah.koperasi.domain.repository;

import com.anjasferdiansyah.koperasi.domain.model.Member;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {

    boolean existsByEmail(String email);
    boolean existsByNik(String nik);

    Optional<Member> findById(UUID id);

    Member save(Member member);
}
