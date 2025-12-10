package com.example.studentapi.dto;

import java.util.List;

public record PaginatedResponseDto<T>(
        List<T> items,
        int currentPage,
        int size,
        long totalItems,
        int totalPages,
        String previousPageUrl,
        String nextPageUrl
) {}