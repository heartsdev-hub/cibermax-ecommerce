package com.cibermax.ecommerce.modules.subCategoria.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Información de una subcategoría")
public record SubCategoryResponse(
        @Schema(
                description = "Identificador único de la subcategoría",
                example = "7c5e3f2e-6d6c-4c2a-8e9d-2f2e4f8f0b11",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        UUID id,
        @Schema(description = "Nombre de la subcategoría", example = "Laptops")
        String nombre,
        @Schema(
                description = "UUID de la categoría padre",
                example = "550e8400-e29b-41d4-a716-446655440000",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        UUID categoriaId,
        @Schema(
                description = "Nombre de la categoría padre",
                example = "Computadoras",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String categoriaNombre,
        @Schema(
                description = "Fecha y hora de creación",
                example = "2026-11-16T10:30:00Z",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant createdAt,
        @Schema(
                description = "Fecha y hora de última actualización",
                example = "2026-11-16T10:30:00Z",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant updatedAt
) {
}
