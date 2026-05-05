package com.learn.todo.dto;

import java.time.LocalDateTime;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Response DTO
//
// This is what we SEND BACK to the client. Notice:
//   - It includes 'id' and 'createdAt' (server-generated, never in the request)
//   - It includes 'completed' (so the client knows the status)
//   - It does NOT include anything sensitive
//
// The server sends: { "id": 1, "title": "Buy milk", "completed": false, "createdAt": "..." }
// ══════════════════════════════════════════════════════════════════════════════
public record TodoResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        LocalDateTime createdAt
) {}

