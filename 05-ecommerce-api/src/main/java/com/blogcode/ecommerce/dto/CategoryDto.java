package com.blogcode.ecommerce.dto;

/**
 * Read-model DTO returned to API clients whenever a {@code Category} is
 * exposed over HTTP.
 *
 * <p><strong>Why this class exists:</strong> we never return JPA entities
 * directly from controllers. Doing so risks leaking lazy-loading proxies
 * (causing {@code LazyInitializationException} or infinite JSON recursion
 * through bidirectional relationships) and tightly couples the public API
 * shape to the database schema. A Java {@code record} is a perfect fit for
 * a DTO: it is immutable, has structural {@code equals()}/{@code hashCode()}
 * for free, and its compact constructor makes intent obvious at a glance.
 *
 * @param id          database identifier of the category
 * @param name        unique, human-readable category name
 * @param description short marketing description of the category
 */
public record CategoryDto(Long id, String name, String description) {
}
