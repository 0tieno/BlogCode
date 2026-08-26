package com.blogcode.ecommerce.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Generic wrapper that flattens a Spring Data {@link Page} into a simple,
 * stable JSON shape for API clients.
 *
 * <p><strong>Why this class exists:</strong> {@link Page} is a great
 * abstraction inside the application, but serializing it directly to JSON
 * exposes internal Spring Data implementation details (e.g. {@code pageable},
 * {@code sort.unsorted}) that can change between Spring versions and are
 * confusing for beginners consuming the API. Wrapping it in our own
 * {@code record} gives us a small, intentional, versionable contract:
 * content + a handful of paging metadata fields.
 *
 * @param <T>          the type of element being paginated (usually a DTO)
 * @param content      the items on the current page
 * @param page         zero-based current page index
 * @param size         requested page size
 * @param totalElements total number of elements across all pages
 * @param totalPages   total number of pages available
 * @param last         whether this is the last page
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last) {

    /**
     * Builds a {@link PageResponse} from a Spring Data {@link Page},
     * centralizing the field-by-field translation in one place so
     * controllers stay simple one-liners.
     *
     * @param page the Spring Data page to adapt
     * @param <T>  the element type
     * @return an equivalent, API-stable {@link PageResponse}
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
