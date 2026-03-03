package com.anjasferdiansyah.koperasi.application.usecase.savings.open_account;

import com.anjasferdiansyah.koperasi.domain.exception.DomainValidationException;
import com.anjasferdiansyah.koperasi.domain.exception.DuplicateSavingsAccountException;
import com.anjasferdiansyah.koperasi.domain.exception.MemberNotFoundException;
import com.anjasferdiansyah.koperasi.domain.model.member.Member;
import com.anjasferdiansyah.koperasi.domain.model.member.MemberStatus;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsAccount;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import com.anjasferdiansyah.koperasi.domain.repository.SavingsAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OpenSavingsAccountService implements OpenSavingsAccountUseCase {

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

        SavingsAccount account = SavingsAccount.open(
                UUID.randomUUID(),
                command.memberId(),
                command.accountNo(),
                command.savingsType(),
                LocalDateTime.now(ZoneOffset.UTC)
        );

        SavingsAccount saved = savingsAccountRepository.save(account);

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
}
