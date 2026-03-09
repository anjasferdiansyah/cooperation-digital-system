package com.anjasferdiansyah.koperasi.application.usecase.savings.open_account;

import com.anjasferdiansyah.koperasi.domain.exception.DomainValidationException;
import com.anjasferdiansyah.koperasi.domain.exception.DuplicateSavingsAccountException;
import com.anjasferdiansyah.koperasi.domain.exception.MemberNotFoundException;
import com.anjasferdiansyah.koperasi.domain.model.member.Member;
import com.anjasferdiansyah.koperasi.domain.model.member.MemberStatus;
import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsAccount;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsType;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import com.anjasferdiansyah.koperasi.domain.repository.SavingsAccountRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenSavingsAccountServiceTest {

    @Test
    void shouldOpenSavingsAccountForActiveMember() {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        InMemorySavingsAccountRepository savingsRepository = new InMemorySavingsAccountRepository();
        OpenSavingsAccountService service = new OpenSavingsAccountService(memberRepository, savingsRepository);

        Member member = registerActiveMember("active-member@mail.com");
        memberRepository.save(member);

        OpenSavingsAccountResult result = service.execute(
                new OpenSavingsAccountCommand(member.getId(), SavingsType.SUKARELA)
        );

        assertNotNull(result.id());
        assertEquals(member.getId(), result.memberId());
        assertEquals(SavingsType.SUKARELA, result.savingsType());
        assertEquals("ACTIVE", result.status().name());
        assertEquals(0, result.balance().signum());
        assertTrue(result.accountNo().startsWith("SAV-SUK-"));
    }

    @Test
    void shouldRejectWhenMemberNotFound() {
        OpenSavingsAccountService service = new OpenSavingsAccountService(
                new InMemoryMemberRepository(),
                new InMemorySavingsAccountRepository()
        );

        assertThrows(
                MemberNotFoundException.class,
                () -> service.execute(new OpenSavingsAccountCommand(UUID.randomUUID(), SavingsType.SUKARELA))
        );
    }

    @Test
    void shouldRejectWhenMemberIsNotActive() {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        InMemorySavingsAccountRepository savingsRepository = new InMemorySavingsAccountRepository();
        OpenSavingsAccountService service = new OpenSavingsAccountService(memberRepository, savingsRepository);

        Member pendingKycMember = Member.register(
                UUID.randomUUID(),
                "Pending User",
                "pending@mail.com",
                "Jakarta",
                "3173010101010001",
                "6281234567001",
                LocalDateTime.now(ZoneOffset.UTC)
        );
        memberRepository.save(pendingKycMember);

        assertThrows(
                DomainValidationException.class,
                () -> service.execute(new OpenSavingsAccountCommand(pendingKycMember.getId(), SavingsType.WAJIB))
        );
    }

    @Test
    void shouldRejectDuplicateAccountByMemberAndType() {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        InMemorySavingsAccountRepository savingsRepository = new InMemorySavingsAccountRepository();
        OpenSavingsAccountService service = new OpenSavingsAccountService(memberRepository, savingsRepository);

        Member member = registerActiveMember("duplicate@mail.com");
        memberRepository.save(member);

        service.execute(new OpenSavingsAccountCommand(member.getId(), SavingsType.POKOK));

        assertThrows(
                DuplicateSavingsAccountException.class,
                () -> service.execute(new OpenSavingsAccountCommand(member.getId(), SavingsType.POKOK))
        );
    }

    private Member registerActiveMember(String email) {
        Member member = Member.register(
                UUID.randomUUID(),
                "Active User",
                email,
                "Jakarta",
                "3173010101010099",
                "6281234567099",
                LocalDateTime.now(ZoneOffset.UTC)
        );
        member.approveKyc("reviewer", LocalDateTime.now(ZoneOffset.UTC));
        return member;
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
            return new PageResult<>(List.of(), page, size, 0, 0, false, false);
        }

        @Override
        public PageResult<Member> search(String keyword, int page, int size, String sortBy, String sortDir) {
            return new PageResult<>(List.of(), page, size, 0, 0, false, false);
        }

        @Override
        public Member save(Member member) {
            storage.put(member.getId(), member);
            return member;
        }
    }

    private static class InMemorySavingsAccountRepository implements SavingsAccountRepository {

        private final Map<UUID, SavingsAccount> storage = new HashMap<>();

        @Override
        public boolean existsByMemberIdAndType(UUID memberId, SavingsType savingsType) {
            return storage.values().stream()
                    .anyMatch(account -> account.getMemberId().equals(memberId) && account.getType() == savingsType);
        }

        @Override
        public Optional<SavingsAccount> findById(UUID id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public SavingsAccount save(SavingsAccount savingsAccount) {
            storage.put(savingsAccount.getId(), savingsAccount);
            return savingsAccount;
        }
    }
}
