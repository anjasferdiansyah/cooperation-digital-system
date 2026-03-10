package com.anjasferdiansyah.koperasi.application.usecase.savings.view_transactions;

import com.anjasferdiansyah.koperasi.domain.model.member.PageResult;

public interface ListSavingsTransactionsUseCase {

    PageResult<ListSavingsTransactionsItemResult> execute(ListSavingsTransactionsCommand command);
}
