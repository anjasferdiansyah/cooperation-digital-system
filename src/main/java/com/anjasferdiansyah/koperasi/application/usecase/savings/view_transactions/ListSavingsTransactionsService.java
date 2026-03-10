package com.anjasferdiansyah.koperasi.application.usecase.savings.view_transactions;

import com.anjasferdiansyah.koperasi.domain.exception.DomainValidationException;
import com.anjasferdiansyah.koperasi.domain.exception.SavingsAccountNotFoundException;
import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsTransaction;
import com.anjasferdiansyah.koperasi.domain.repository.SavingsAccountRepository;
import com.anjasferdiansyah.koperasi.domain.repository.SavingsTransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
public class ListSavingsTransactionsService implements ListSavingsTransactionsUseCase {

    private static final Set<String> ALLOWED_SORT_BY = Set.of(
            "occurredAt",
            "createdAt",
            "amount",
            "transactionType"
    );

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsTransactionRepository savingsTransactionRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<ListSavingsTransactionsItemResult> execute(ListSavingsTransactionsCommand command) {
        if (savingsAccountRepository.findById(command.accountId()).isEmpty()) {
            throw new SavingsAccountNotFoundException(command.accountId());
        }

        String sortBy = normalizeSortBy(command.sortBy());
        String sortDir = normalizeSortDir(command.sortDir());
        PageResult<SavingsTransaction> page = savingsTransactionRepository.findBySavingsAccountId(
                command.accountId(),
                command.page(),
                command.size(),
                sortBy,
                sortDir
        );

        return new PageResult<>(
                page.items().stream().map(this::toItemResult).toList(),
                page.page(),
                page.size(),
                page.totalItems(),
                page.totalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    private String normalizeSortBy(String sortBy) {
        String value = (sortBy == null || sortBy.isBlank()) ? "occurredAt" : sortBy.trim();
        if (!ALLOWED_SORT_BY.contains(value)) {
            throw new DomainValidationException("Invalid sortBy: " + sortBy);
        }
        return value;
    }

    private String normalizeSortDir(String sortDir) {
        String value = (sortDir == null || sortDir.isBlank()) ? "desc" : sortDir.trim().toLowerCase();
        if (!"asc".equals(value) && !"desc".equals(value)) {
            throw new DomainValidationException("Invalid sortDir: " + sortDir);
        }
        return value;
    }

    private ListSavingsTransactionsItemResult toItemResult(SavingsTransaction transaction) {
        return new ListSavingsTransactionsItemResult(
                transaction.getId(),
                transaction.getSavingsAccountId(),
                transaction.getMemberId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getReferenceNo(),
                transaction.getExternalReference(),
                transaction.getNote(),
                transaction.getOccurredAt()
        );
    }
}
