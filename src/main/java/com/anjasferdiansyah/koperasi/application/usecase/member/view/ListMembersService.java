package com.anjasferdiansyah.koperasi.application.usecase.member.view;

import com.anjasferdiansyah.koperasi.domain.exception.DomainValidationException;
import com.anjasferdiansyah.koperasi.domain.model.member.Member;
import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
public class ListMembersService implements ListMembersUseCase {

    private static final Set<String> ALLOWED_SORT_BY = Set.of(
            "fullName",
            "email",
            "nik",
            "phoneNumber",
            "status",
            "registeredAt",
            "createdAt",
            "updatedAt"
    );

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<ListMembersItemResult> execute(ListMembersCommand command) {
        String sortBy = normalizeSortBy(command.sortBy());
        String sortDir = normalizeSortDir(command.sortDir());

        PageResult<Member> page = (command.search() == null || command.search().isBlank())
                ? memberRepository.findAll(command.page(), command.size(), sortBy, sortDir)
                : memberRepository.search(command.search(), command.page(), command.size(), sortBy, sortDir);

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
        String value = (sortBy == null || sortBy.isBlank()) ? "registeredAt" : sortBy.trim();
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

    private ListMembersItemResult toItemResult(Member member) {
        return new ListMembersItemResult(
                member.getId(),
                member.getFullName(),
                member.getEmail(),
                member.getAddress(),
                member.getNik(),
                member.getPhoneNumber(),
                member.getStatus(),
                member.getRegisteredAt(),
                member.getKycReviewedAt(),
                member.getKycReviewedBy()
        );
    }
}
