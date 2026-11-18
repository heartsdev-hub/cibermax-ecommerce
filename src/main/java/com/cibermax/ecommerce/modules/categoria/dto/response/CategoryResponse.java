package com.cibermax.ecommerce.modules.categoria.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Información de una categoría")
public record CategoryResponse(
        @Schema(
                description = "Identificador único de la categoría",
                example = "550e8400-e29b-41d4-a716-446655440000",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        UUID id,
        @Schema(
                description = "Código de la categoría",
                example = "10",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String codigo,
        @Schema(
                description = "Nombre de la categoría",
                example = "Almacenamiento"
        )
        String nombre,
        @Schema(
                description = "Fecha y hora de creación",
                example = "2026-11-15T10:30:00Z",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant createdAt,
        @Schema(
                description = "Fecha y hora de última actualización",
                example = "2026-11-15T11:00:00Z",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant updatedAt
) {

    public CategoryResponse(UUID id, String nombre, Instant createdAt, Instant updatedAt) {
        this(id, null, nombre, createdAt, updatedAt);
    }
}
