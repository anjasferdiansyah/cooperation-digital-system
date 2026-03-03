package com.anjasferdiansyah.koperasi.application.usecase.member.update;

import com.anjasferdiansyah.koperasi.domain.exception.DuplicateMemberEmailException;
import com.anjasferdiansyah.koperasi.domain.exception.DuplicateMemberNikException;
import com.anjasferdiansyah.koperasi.domain.exception.DuplicateMemberPhoneNumberException;
import com.anjasferdiansyah.koperasi.domain.exception.MemberNotFoundException;
import com.anjasferdiansyah.koperasi.domain.model.member.Member;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@AllArgsConstructor
public class UpdateMemberService implements UpdateMemberUseCase {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UpdateMemberResult execute(UpdateMemberCommand command) {
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new MemberNotFoundException(command.memberId()));

        String normalizedEmail = command.email().trim().toLowerCase(Locale.ROOT);
        String normalizedNik = command.nik().trim();
        String normalizedPhoneNumber = command.phoneNumber().trim();

        if (!member.getEmail().equals(normalizedEmail) && memberRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateMemberEmailException(command.email());
        }

        if (!member.getNik().equals(normalizedNik) && memberRepository.existsByNik(normalizedNik)) {
            throw new DuplicateMemberNikException(command.nik());
        }

        if (!member.getPhoneNumber().equals(normalizedPhoneNumber)
                && memberRepository.existsByPhoneNumber(normalizedPhoneNumber)) {
            throw new DuplicateMemberPhoneNumberException(command.phoneNumber());
        }

        member.updateProfile(
                command.fullName(),
                command.email(),
                command.address(),
                command.nik(),
                command.phoneNumber()
        );

        Member saved = memberRepository.save(member);

        return new UpdateMemberResult(
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
