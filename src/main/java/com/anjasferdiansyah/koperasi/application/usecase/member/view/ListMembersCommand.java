package com.anjasferdiansyah.koperasi.application.usecase.member.view;

public record ListMembersCommand(
        String search,
        int page,
        int size,
        String sortBy,
        String sortDir
) {
}
