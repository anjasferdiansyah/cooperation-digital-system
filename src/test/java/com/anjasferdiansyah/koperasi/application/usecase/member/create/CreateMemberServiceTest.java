package com.anjasferdiansyah.koperasi.application.usecase.member.create;

import com.anjasferdiansyah.koperasi.domain.exception.DuplicateMemberEmailException;
import com.anjasferdiansyah.koperasi.domain.exception.DuplicateMemberNikException;
import com.anjasferdiansyah.koperasi.domain.model.member.Member;
import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Test
    void shouldRejectDuplicateNik() {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();
        CreateMemberService service = new CreateMemberService(repository);

        service.execute(new CreateMemberCommand("Anjas", "anjas@mail.com", "Jakarta", "3173010101010001", "6281234567890"));

        assertThrows(
                DuplicateMemberNikException.class,
                () -> service.execute(new CreateMemberCommand("Budi", "budi@mail.com", "Bandung", "3173010101010001", "6281234567891"))
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
        public boolean existsByPhoneNumber(String phoneNumber) {
            return storage.values().stream().anyMatch(member -> member.getPhoneNumber().equals(phoneNumber));
        }

        @Override
        public Optional<Member> findById(UUID id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public PageResult<Member> findAll(int page, int size, String sortBy, String sortDir) {
            List<Member> members = storage.values().stream()
                    .sorted(Comparator.comparing(Member::getRegisteredAt))
                    .toList();
            return toPageResult(members, page, size);
        }

        @Override
        public PageResult<Member> search(String keyword, int page, int size, String sortBy, String sortDir) {
            List<Member> members = storage.values().stream()
                    .filter(member -> member.getFullName().toLowerCase().contains(keyword.toLowerCase())
                            || member.getEmail().toLowerCase().contains(keyword.toLowerCase())
                            || member.getNik().contains(keyword)
                            || member.getPhoneNumber().contains(keyword))
                    .sorted(Comparator.comparing(Member::getRegisteredAt))
                    .toList();
            return toPageResult(members, page, size);
        }

        @Override
        public Member save(Member member) {
            storage.put(member.getId(), member);
            return member;
        }

        private PageResult<Member> toPageResult(List<Member> source, int page, int size) {
            int fromIndex = Math.min(page * size, source.size());
            int toIndex = Math.min(fromIndex + size, source.size());
            List<Member> items = source.subList(fromIndex, toIndex);
            int totalPages = source.isEmpty() ? 0 : (int) Math.ceil((double) source.size() / size);

            return new PageResult<>(
                    items,
                    page,
                    size,
                    source.size(),
                    totalPages,
                    page + 1 < totalPages,
                    page > 0
            );
        }
    }
}
