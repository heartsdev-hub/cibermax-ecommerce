package com.cibermax.ecommerce.modules.categoria.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String nombre,
        Instant createdAt,
        Instant updatedAt
) {
}
