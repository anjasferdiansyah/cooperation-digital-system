package com.anjasferdiansyah.koperasi.presentation.response;

public record ApiMetaResponse(
        Integer page,
        Integer size,
        Long totalItems,
        Integer totalPages,
        Boolean hasNext,
        Boolean hasPrevious
) {
}
