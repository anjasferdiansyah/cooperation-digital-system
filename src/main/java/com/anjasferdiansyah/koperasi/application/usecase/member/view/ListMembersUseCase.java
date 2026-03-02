package com.anjasferdiansyah.koperasi.application.usecase.member.view;

import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;

public interface ListMembersUseCase {

    PageResult<ListMembersItemResult> execute(ListMembersCommand command);
}
