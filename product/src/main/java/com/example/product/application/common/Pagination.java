package com.example.product.application.common;

import java.util.List;

public record Pagination<T>(
        List<T> data,
        int currentPage,
        int totalPage,
        long totalElements
) {
}
