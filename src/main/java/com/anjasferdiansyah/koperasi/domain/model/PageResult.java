package com.anjasferdiansyah.koperasi.domain.model;

import java.util.List;

public record PageResult<T>(
        List<T> items,
        int page,
        int size,
        long totalItems,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
}
