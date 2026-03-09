package com.anjasferdiansyah.koperasi.application.usecase.savings.open_account;

import com.anjasferdiansyah.koperasi.domain.exception.DomainValidationException;
import com.anjasferdiansyah.koperasi.domain.exception.DuplicateSavingsAccountException;
import com.anjasferdiansyah.koperasi.domain.exception.MemberNotFoundException;
import com.anjasferdiansyah.koperasi.domain.model.member.Member;
import com.anjasferdiansyah.koperasi.domain.model.member.MemberStatus;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsAccount;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsType;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import com.anjasferdiansyah.koperasi.domain.repository.SavingsAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OpenSavingsAccountService implements OpenSavingsAccountUseCase {

    private static final DateTimeFormatter ACCOUNT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int MAX_ACCOUNT_NO_RETRIES = 3;

    private final MemberRepository memberRepository;
    private final SavingsAccountRepository savingsAccountRepository;

    @Override
    @Transactional
    public OpenSavingsAccountResult execute(OpenSavingsAccountCommand command) {
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new MemberNotFoundException(command.memberId()));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new DomainValidationException("Only active member can open savings account");
        }

        if (savingsAccountRepository.existsByMemberIdAndType(command.memberId(), command.savingsType())) {
            throw new DuplicateSavingsAccountException(command.memberId(), command.savingsType());
        }

        SavingsAccount saved = saveWithGeneratedAccountNo(command);

        return new OpenSavingsAccountResult(
                saved.getId(),
                saved.getMemberId(),
                saved.getAccountNo(),
                saved.getType(),
                saved.getStatus(),
                saved.getBalance(),
                saved.getOpenedAt()
        );
    }

    private SavingsAccount saveWithGeneratedAccountNo(OpenSavingsAccountCommand command) {
        for (int attempt = 1; attempt <= MAX_ACCOUNT_NO_RETRIES; attempt++) {
            SavingsAccount account = SavingsAccount.open(
                    UUID.randomUUID(),
                    command.memberId(),
                    generateAccountNo(command.savingsType()),
                    command.savingsType(),
                    LocalDateTime.now(ZoneOffset.UTC)
            );

            try {
                return savingsAccountRepository.save(account);
            } catch (DataIntegrityViolationException ex) {
                if (isMemberTypeConflict(ex)) {
                    throw new DuplicateSavingsAccountException(command.memberId(), command.savingsType());
                }
                if (isAccountNoConflict(ex) && attempt < MAX_ACCOUNT_NO_RETRIES) {
                    continue;
                }
                throw ex;
            }
        }
        throw new DomainValidationException("Failed to generate unique savings account number");
    }

    private boolean isMemberTypeConflict(DataIntegrityViolationException ex) {
        String message = getErrorMessage(ex);
        return message.contains("uq_savings_accounts_member_type");
    }

    private boolean isAccountNoConflict(DataIntegrityViolationException ex) {
        String message = getErrorMessage(ex);
        return message.contains("uq_savings_accounts_account_no");
    }

    private String getErrorMessage(Exception ex) {
        Throwable root = ex.getCause();
        while (root != null && root.getCause() != null) {
            root = root.getCause();
        }
        if (root == null || root.getMessage() == null) {
            return "";
        }
        return root.getMessage().toLowerCase(Locale.ROOT);
    }

    private String generateAccountNo(SavingsType savingsType) {
        String typePrefix = savingsType.name().substring(0, 3);
        String datePart = LocalDateTime.now(ZoneOffset.UTC).format(ACCOUNT_DATE_FORMATTER);
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
        return "SAV-" + typePrefix + "-" + datePart + "-" + randomPart;
    }
}
