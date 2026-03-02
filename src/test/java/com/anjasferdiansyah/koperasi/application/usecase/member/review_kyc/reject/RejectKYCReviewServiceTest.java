package com.anjasferdiansyah.koperasi.application.usecase.member.review_kyc.reject;

import com.anjasferdiansyah.koperasi.domain.exception.MemberNotFoundException;
import com.anjasferdiansyah.koperasi.domain.model.Member;
import com.anjasferdiansyah.koperasi.domain.model.MemberStatus;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RejectKYCReviewServiceTest {

    @Test
    void shouldRejectKyc() {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();
        Member member = Member.register(
                UUID.randomUUID(),
                "Anjas",
                "anjas@mail.com",
                "Jakarta",
                "3173010101010001",
                "6281234567890",
                LocalDateTime.now()
        );
        repository.save(member);

        RejectKYCReviewService service = new RejectKYCReviewService(repository);
        RejectKYCReviewResult result = service.execute(
                new RejectKYCReviewCommand(member.getId(), "admin.kyc")
        );

        assertEquals(member.getId(), result.memberId());
        assertEquals(MemberStatus.REJECTED, result.status());
        assertEquals("admin.kyc", result.kycReviewedBy());
    }

    @Test
    void shouldFailWhenMemberNotFound() {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();
        RejectKYCReviewService service = new RejectKYCReviewService(repository);

        assertThrows(
                MemberNotFoundException.class,
                () -> service.execute(new RejectKYCReviewCommand(UUID.randomUUID(), "admin.kyc"))
        );
    }

    private static class InMemoryMemberRepository implements MemberRepository {
        private final Map<UUID, Member> storage = new HashMap<>();

        @Override
        public boolean existsByEmail(String email) {
            return storage.values().stream().anyMatch(member -> member.getEmail().equals(email));
        }

        @Override
        public boolean existsByNik(String nik) {
            return storage.values().stream().anyMatch(member -> member.getNik().equals(nik));
        }

        @Override
        public Optional<Member> findById(UUID id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public Member save(Member member) {
            storage.put(member.getId(), member);
            return member;
        }
    }
}
