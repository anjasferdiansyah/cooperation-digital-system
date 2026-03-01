package com.anjasferdiansyah.koperasi.application.usecase.member.create;

import com.anjasferdiansyah.koperasi.domain.exception.DuplicateMemberEmailException;
import com.anjasferdiansyah.koperasi.domain.model.Member;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class CreateMemberService implements CreateMemberUseCase {

    private final MemberRepository memberRepository;

    public CreateMemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public CreateMemberResult execute(CreateMemberCommand command) {
        if (memberRepository.existsByEmail(command.email())) {
            throw new DuplicateMemberEmailException(command.email());
        }

        Member member = Member.register(
                UUID.randomUUID(),
                command.fullName(),
                command.email(),
                command.address(),
                command.nik(),
                command.phoneNumber(),
                LocalDateTime.now(ZoneOffset.UTC)
        );

        Member saved = memberRepository.save(member);
        return new CreateMemberResult(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getAddress(),
                saved.getNik(),
                saved.getPhoneNumber(),
                saved.getRegisteredAt()
        );
    }
}
