package com.anjasferdiansyah.koperasi.application.usecase.savings.deposit;

import com.anjasferdiansyah.koperasi.domain.exception.SavingsAccountNotFoundException;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsAccount;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsTransaction;
import com.anjasferdiansyah.koperasi.domain.repository.SavingsAccountRepository;
import com.anjasferdiansyah.koperasi.domain.repository.SavingsTransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DepositSavingsService implements DepositSavingsUseCase {

    private static final DateTimeFormatter REFERENCE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsTransactionRepository savingsTransactionRepository;

    @Override
    @Transactional
    public DepositSavingsResult execute(DepositSavingsCommand command) {
        SavingsAccount account = savingsAccountRepository.findById(command.accountId())
                .orElseThrow(() -> new SavingsAccountNotFoundException(command.accountId()));

        LocalDateTime occurredAt = command.occurredAt() == null
                ? LocalDateTime.now(ZoneOffset.UTC)
                : command.occurredAt();

        SavingsTransaction transaction = account.deposit(
                command.amount(),
                generateReferenceNo(),
                command.externalReference(),
                command.note(),
                occurredAt
        );

        SavingsTransaction savedTransaction = savingsTransactionRepository.save(transaction);
        SavingsAccount savedAccount = savingsAccountRepository.save(account);


        return new DepositSavingsResult(
                savedTransaction.getId(),
                savedTransaction.getSavingsAccountId(),
                savedTransaction.getMemberId(),
                savedTransaction.getType(),
                savedTransaction.getAmount(),
                savedAccount.getBalance(),
                savedTransaction.getReferenceNo(),
                savedTransaction.getExternalReference(),
                savedTransaction.getNote(),
                savedTransaction.getOccurredAt()
        );
    }

    private String generateReferenceNo() {
        String timestamp = LocalDateTime.now(ZoneOffset.UTC).format(REFERENCE_TIME_FORMATTER);
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
        return "TRX-DEP-" + timestamp + "-" + randomPart;
    }
}
