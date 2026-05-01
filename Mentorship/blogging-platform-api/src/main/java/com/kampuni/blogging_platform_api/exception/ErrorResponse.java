package com.kampuni.blogging_platform_api.exception;

import java.time.LocalDateTime;

public record ErrorResponse(String message, int status, LocalDateTime timestamp) {

}
