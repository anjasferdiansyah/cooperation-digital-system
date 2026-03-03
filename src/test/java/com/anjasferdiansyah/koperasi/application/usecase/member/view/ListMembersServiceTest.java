package com.anjasferdiansyah.koperasi.application.usecase.member.view;

import com.anjasferdiansyah.koperasi.domain.model.member.Member;
import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListMembersServiceTest {

    @Test
    void shouldViewMembersWithPagination() {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();
        repository.save(member("Anjas", "anjas@mail.com", "3173010101010001", "6281234567890", LocalDateTime.of(2026, 1, 1, 10, 0)));
        repository.save(member("Budi", "budi@mail.com", "3173010101010002", "6281234567891", LocalDateTime.of(2026, 1, 2, 10, 0)));
        repository.save(member("Cici", "cici@mail.com", "3173010101010003", "6281234567892", LocalDateTime.of(2026, 1, 3, 10, 0)));

        ListMembersService service = new ListMembersService(repository);
        PageResult<ListMembersItemResult> result = service.execute(
                new ListMembersCommand(null, 0, 2, "registeredAt", "asc")
        );

        assertEquals(2, result.items().size());
        assertEquals("Anjas", result.items().getFirst().fullName());
        assertEquals(3, result.totalItems());
        assertEquals(2, result.totalPages());
        assertEquals(true, result.hasNext());
        assertEquals(false, result.hasPrevious());
    }

    @Test
    void shouldSearchMembersByKeyword() {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();
        repository.save(member("Anjas Ferdiansyah", "anjas@mail.com", "3173010101010001", "6281234567890", LocalDateTime.of(2026, 1, 1, 10, 0)));
        repository.save(member("Budi Santoso", "budi@mail.com", "3173010101010002", "6281234567891", LocalDateTime.of(2026, 1, 2, 10, 0)));

        ListMembersService service = new ListMembersService(repository);
        PageResult<ListMembersItemResult> result = service.execute(
                new ListMembersCommand("anjas", 0, 10, "registeredAt", "desc")
        );

        assertEquals(1, result.items().size());
        assertEquals("Anjas Ferdiansyah", result.items().getFirst().fullName());
        assertEquals(1, result.totalItems());
        assertEquals(false, result.hasNext());
    }

    private Member member(String fullName, String email, String nik, String phoneNumber, LocalDateTime registeredAt) {
        return Member.register(
                UUID.randomUUID(),
                fullName,
                email,
                "Jakarta",
                nik,
                phoneNumber,
                registeredAt
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
            List<Member> members = sortedMembers(storage.values().stream().toList(), sortBy, sortDir);
            return toPageResult(members, page, size);
        }

        @Override
        public PageResult<Member> search(String keyword, int page, int size, String sortBy, String sortDir) {
            String normalized = keyword.toLowerCase();
            List<Member> filtered = storage.values().stream()
                    .filter(member -> member.getFullName().toLowerCase().contains(normalized)
                            || member.getEmail().toLowerCase().contains(normalized)
                            || member.getNik().contains(keyword)
                            || member.getPhoneNumber().contains(keyword))
                    .toList();
            List<Member> members = sortedMembers(filtered, sortBy, sortDir);
            return toPageResult(members, page, size);
        }

        @Override
        public Member save(Member member) {
            storage.put(member.getId(), member);
            return member;
        }

        private List<Member> sortedMembers(List<Member> source, String sortBy, String sortDir) {
            Comparator<Member> comparator = switch (sortBy) {
                case "fullName" -> Comparator.comparing(Member::getFullName);
                case "email" -> Comparator.comparing(Member::getEmail);
                case "nik" -> Comparator.comparing(Member::getNik);
                case "phoneNumber" -> Comparator.comparing(Member::getPhoneNumber);
                case "status" -> Comparator.comparing(member -> member.getStatus().name());
                case "registeredAt" -> Comparator.comparing(Member::getRegisteredAt);
                case "createdAt" -> Comparator.comparing(Member::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo));
                case "updatedAt" -> Comparator.comparing(Member::getUpdatedAt, Comparator.nullsLast(LocalDateTime::compareTo));
                default -> Comparator.comparing(Member::getRegisteredAt);
            };

            if ("desc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }
            return source.stream().sorted(comparator).toList();
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
