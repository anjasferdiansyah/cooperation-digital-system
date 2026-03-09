package com.anjasferdiansyah.koperasi.application.usecase.savings.transaction;

import com.anjasferdiansyah.koperasi.application.usecase.savings.deposit.DepositSavingsCommand;
import com.anjasferdiansyah.koperasi.application.usecase.savings.deposit.DepositSavingsResult;
import com.anjasferdiansyah.koperasi.application.usecase.savings.deposit.DepositSavingsService;
import com.anjasferdiansyah.koperasi.application.usecase.savings.withdraw.WithdrawSavingsCommand;
import com.anjasferdiansyah.koperasi.application.usecase.savings.withdraw.WithdrawSavingsResult;
import com.anjasferdiansyah.koperasi.application.usecase.savings.withdraw.WithdrawSavingsService;
import com.anjasferdiansyah.koperasi.domain.exception.DomainValidationException;
import com.anjasferdiansyah.koperasi.domain.exception.SavingsAccountNotFoundException;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsAccount;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsTransaction;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsType;
import com.anjasferdiansyah.koperasi.domain.repository.SavingsAccountRepository;
import com.anjasferdiansyah.koperasi.domain.repository.SavingsTransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SavingsTransactionServiceTest {

    @Test
    void shouldDepositToSavingsAccount() {
        InMemorySavingsAccountRepository accountRepository = new InMemorySavingsAccountRepository();
        InMemorySavingsTransactionRepository transactionRepository = new InMemorySavingsTransactionRepository();
        DepositSavingsService service = new DepositSavingsService(accountRepository, transactionRepository);

        SavingsAccount account = createAccount();
        accountRepository.save(account);

        DepositSavingsResult result = service.execute(new DepositSavingsCommand(
                account.getId(),
                BigDecimal.valueOf(50_000),
                "EXT-DEP-20260307-0001",
                "Setoran wajib",
                LocalDateTime.now(ZoneOffset.UTC)
        ));

        assertNotNull(result.transactionId());
        assertEquals(account.getId(), result.accountId());
        assertEquals(0, BigDecimal.valueOf(50_000).compareTo(result.amount()));
        assertEquals(0, BigDecimal.valueOf(50_000).compareTo(result.balanceAfter()));
        assertEquals("EXT-DEP-20260307-0001", result.externalReference());
    }

    @Test
    void shouldWithdrawFromSavingsAccount() {
        InMemorySavingsAccountRepository accountRepository = new InMemorySavingsAccountRepository();
        InMemorySavingsTransactionRepository transactionRepository = new InMemorySavingsTransactionRepository();
        WithdrawSavingsService service = new WithdrawSavingsService(accountRepository, transactionRepository);

        SavingsAccount account = createAccount();
        account.deposit(
                BigDecimal.valueOf(100_000),
                "DEP-INIT-0001",
                null,
                "Initial deposit",
                LocalDateTime.now(ZoneOffset.UTC)
        );
        accountRepository.save(account);

        WithdrawSavingsResult result = service.execute(new WithdrawSavingsCommand(
                account.getId(),
                BigDecimal.valueOf(40_000),
                "EXT-WDR-20260307-0001",
                "Penarikan sukarela",
                LocalDateTime.now(ZoneOffset.UTC)
        ));

        assertNotNull(result.transactionId());
        assertEquals(account.getId(), result.accountId());
        assertEquals(0, BigDecimal.valueOf(40_000).compareTo(result.amount()));
        assertEquals(0, BigDecimal.valueOf(60_000).compareTo(result.balanceAfter()));
        assertEquals("EXT-WDR-20260307-0001", result.externalReference());
    }

    @Test
    void shouldRejectDepositWhenAccountNotFound() {
        DepositSavingsService service = new DepositSavingsService(
                new InMemorySavingsAccountRepository(),
                new InMemorySavingsTransactionRepository()
        );

        assertThrows(
                SavingsAccountNotFoundException.class,
                () -> service.execute(new DepositSavingsCommand(
                        UUID.randomUUID(),
                        BigDecimal.valueOf(10_000),
                        "EXT-DEP-20260307-404",
                        null,
                        LocalDateTime.now(ZoneOffset.UTC)
                ))
        );
    }

    @Test
    void shouldRejectWithdrawWhenInsufficientBalance() {
        InMemorySavingsAccountRepository accountRepository = new InMemorySavingsAccountRepository();
        InMemorySavingsTransactionRepository transactionRepository = new InMemorySavingsTransactionRepository();
        WithdrawSavingsService service = new WithdrawSavingsService(accountRepository, transactionRepository);

        SavingsAccount account = createAccount();
        account.deposit(
                BigDecimal.valueOf(20_000),
                "DEP-INIT-0002",
                null,
                "Initial deposit",
                LocalDateTime.now(ZoneOffset.UTC)
        );
        accountRepository.save(account);

        assertThrows(
                DomainValidationException.class,
                () -> service.execute(new WithdrawSavingsCommand(
                        account.getId(),
                        BigDecimal.valueOf(25_000),
                        "EXT-WDR-20260307-0002",
                        null,
                        LocalDateTime.now(ZoneOffset.UTC)
                ))
        );
    }

    private SavingsAccount createAccount() {
        return SavingsAccount.open(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "SAV-WAJ-20260307-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8),
                SavingsType.WAJIB,
                LocalDateTime.now(ZoneOffset.UTC)
        );
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

    private static class InMemorySavingsTransactionRepository implements SavingsTransactionRepository {

        private final Map<UUID, SavingsTransaction> storage = new HashMap<>();

        @Override
        public SavingsTransaction save(SavingsTransaction transaction) {
            storage.put(transaction.getId(), transaction);
            return transaction;
        }
    }
}
