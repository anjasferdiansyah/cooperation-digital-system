package com.anjasferdiansyah.koperasi.application.usecase.member.create;

import com.anjasferdiansyah.koperasi.domain.exception.DuplicateMemberEmailException;
import com.anjasferdiansyah.koperasi.domain.model.Member;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateMemberServiceTest {

    @Test
    void shouldCreateMember() {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();
        CreateMemberService service = new CreateMemberService(repository);

        CreateMemberResult result = service.execute(
                new CreateMemberCommand("Anjas", "anjas@mail.com", "Jakarta", "3173010101010001", "6281234567890")
        );

        assertEquals("Anjas", result.fullName());
        assertEquals("anjas@mail.com", result.email());
        assertEquals("Jakarta", result.address());
        assertEquals("3173010101010001", result.nik());
        assertEquals("6281234567890", result.phoneNumber());
    }

    @Test
    void shouldRejectDuplicateEmail() {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();
        CreateMemberService service = new CreateMemberService(repository);

        service.execute(new CreateMemberCommand("Anjas", "anjas@mail.com", "Jakarta", "3173010101010001", "6281234567890"));

        assertThrows(
                DuplicateMemberEmailException.class,
                () -> service.execute(new CreateMemberCommand("Budi", "anjas@mail.com", "Bandung", "3173010101010002", "6281234567891"))
        );
    }

    private static class InMemoryMemberRepository implements MemberRepository {

        private final Map<UUID, Member> storage = new HashMap<>();

        @Override
        public boolean existsByEmail(String email) {
            return storage.values().stream().anyMatch(member -> member.getEmail().equals(email));
        }

        @Override
        public Member save(Member member) {
            storage.put(member.getId(), member);
            return member;
        }
    }
}
