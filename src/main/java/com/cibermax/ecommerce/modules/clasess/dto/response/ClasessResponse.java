package com.cibermax.ecommerce.modules.clasess.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Información de una clase asociada a una categoría")
public record ClasessResponse(
        @Schema(description = "Identificador único", accessMode = Schema.AccessMode.READ_ONLY)
        UUID id,
        @Schema(description = "Código compuesto por el código de categoría y la secuencia de clase", example = "1010")
        String codigo,
        @Schema(description = "Nombre de la clase", example = "Audífonos")
        String nombre,
        @Schema(description = "Categoría padre", example = "Audio", accessMode = Schema.AccessMode.READ_ONLY)
        String categoria,
        @Schema(description = "Fecha y hora de creación", accessMode = Schema.AccessMode.READ_ONLY)
        Instant createdAt,
        @Schema(description = "Fecha y hora de última actualización", accessMode = Schema.AccessMode.READ_ONLY)
        Instant updatedAt
) {
}
