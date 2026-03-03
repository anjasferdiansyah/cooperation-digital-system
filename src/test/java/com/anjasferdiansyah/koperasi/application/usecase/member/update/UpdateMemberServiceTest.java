package com.anjasferdiansyah.koperasi.application.usecase.member.update;

import com.anjasferdiansyah.koperasi.domain.exception.DuplicateMemberEmailException;
import com.anjasferdiansyah.koperasi.domain.exception.DuplicateMemberNikException;
import com.anjasferdiansyah.koperasi.domain.exception.DuplicateMemberPhoneNumberException;
import com.anjasferdiansyah.koperasi.domain.exception.MemberNotFoundException;
import com.anjasferdiansyah.koperasi.domain.model.member.Member;
import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateMemberServiceTest {

    @Test
    void shouldUpdateMember() {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();
        Member member = member("Anjas", "anjas@mail.com", "3173010101010001", "6281234567890");
        repository.save(member);

        UpdateMemberService service = new UpdateMemberService(repository);
        UpdateMemberResult result = service.execute(new UpdateMemberCommand(
                member.getId(),
                "Anjas Ferdiansyah",
                "anjas.ferdiansyah@mail.com",
                "Bandung",
                "3173010101010011",
                "6281234567800"
        ));

        assertEquals(member.getId(), result.id());
        assertEquals("Anjas Ferdiansyah", result.fullName());
        assertEquals("anjas.ferdiansyah@mail.com", result.email());
        assertEquals("Bandung", result.address());
        assertEquals("3173010101010011", result.nik());
        assertEquals("6281234567800", result.phoneNumber());
    }

    @Test
    void shouldFailWhenMemberNotFound() {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();
        UpdateMemberService service = new UpdateMemberService(repository);

        assertThrows(MemberNotFoundException.class, () -> service.execute(new UpdateMemberCommand(
                UUID.randomUUID(),
                "Anjas",
                "anjas@mail.com",
                "Jakarta",
                "3173010101010001",
                "6281234567890"
        )));
    }

    @Test
    void shouldFailWhenEmailAlreadyUsedByAnotherMember() {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();
        Member anjas = member("Anjas", "anjas@mail.com", "3173010101010001", "6281234567890");
        Member budi = member("Budi", "budi@mail.com", "3173010101010002", "6281234567891");
        repository.save(anjas);
        repository.save(budi);

        UpdateMemberService service = new UpdateMemberService(repository);

        assertThrows(DuplicateMemberEmailException.class, () -> service.execute(new UpdateMemberCommand(
                anjas.getId(),
                "Anjas",
                "budi@mail.com",
                "Jakarta",
                "3173010101010001",
                "6281234567890"
        )));
    }

    @Test
    void shouldFailWhenNikAlreadyUsedByAnotherMember() {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();
        Member anjas = member("Anjas", "anjas@mail.com", "3173010101010001", "6281234567890");
        Member budi = member("Budi", "budi@mail.com", "3173010101010002", "6281234567891");
        repository.save(anjas);
        repository.save(budi);

        UpdateMemberService service = new UpdateMemberService(repository);

        assertThrows(DuplicateMemberNikException.class, () -> service.execute(new UpdateMemberCommand(
                anjas.getId(),
                "Anjas",
                "anjas@mail.com",
                "Jakarta",
                "3173010101010002",
                "6281234567890"
        )));
    }

    @Test
    void shouldFailWhenPhoneNumberAlreadyUsedByAnotherMember() {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();
        Member anjas = member("Anjas", "anjas@mail.com", "3173010101010001", "6281234567890");
        Member budi = member("Budi", "budi@mail.com", "3173010101010002", "6281234567891");
        repository.save(anjas);
        repository.save(budi);

        UpdateMemberService service = new UpdateMemberService(repository);

        assertThrows(DuplicateMemberPhoneNumberException.class, () -> service.execute(new UpdateMemberCommand(
                anjas.getId(),
                "Anjas",
                "anjas@mail.com",
                "Jakarta",
                "3173010101010001",
                "6281234567891"
        )));
    }

    private Member member(String fullName, String email, String nik, String phoneNumber) {
        return Member.register(
                UUID.randomUUID(),
                fullName,
                email,
                "Jakarta",
                nik,
                phoneNumber,
                LocalDateTime.now()
        );
    }

    private static class InMemoryMemberRepository implements MemberRepository {
        private final Map<UUID, Member> storage = new HashMap<>();

        @Override
        public boolean existsByEmail(String email) {
            String normalized = email.trim().toLowerCase(Locale.ROOT);
            return storage.values().stream().anyMatch(member -> member.getEmail().equals(normalized));
        }

        @Override
        public boolean existsByNik(String nik) {
            String normalized = nik.trim();
            return storage.values().stream().anyMatch(member -> member.getNik().equals(normalized));
        }

        @Override
        public boolean existsByPhoneNumber(String phoneNumber) {
            String normalized = phoneNumber.trim();
            return storage.values().stream().anyMatch(member -> member.getPhoneNumber().equals(normalized));
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
