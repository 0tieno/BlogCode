package com.learn.github_middleware.dto;

import java.time.Instant;

public record ErrorResponse(
        Instant timeStamp,
        int status,
        String error,
        String message,
        String path
) {
}
