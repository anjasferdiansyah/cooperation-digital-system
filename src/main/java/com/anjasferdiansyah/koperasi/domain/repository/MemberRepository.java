package com.anjasferdiansyah.koperasi.domain.repository;

import com.anjasferdiansyah.koperasi.domain.model.Member;

public interface MemberRepository {

    boolean existsByEmail(String email);

    Member save(Member member);
}
