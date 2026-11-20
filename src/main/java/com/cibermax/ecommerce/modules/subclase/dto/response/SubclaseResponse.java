package com.cibermax.ecommerce.modules.subclase.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Información de una subclase asociada a una Clasess")
public record SubclaseResponse(
        @Schema(description = "Identificador único", accessMode = Schema.AccessMode.READ_ONLY)
        UUID id,
        @Schema(description = "Código compuesto por el código de la Clasess y la secuencia de subclase", example = "101010")
        String codigo,
        @Schema(description = "Nombre de la subclase", example = "Audífonos inalámbricos")
        String nombre,
        @Schema(description = "Nombre de la categoría asociada", example = "Audio", accessMode = Schema.AccessMode.READ_ONLY)
        String categoria,
        @Schema(description = "Nombre de la Clasess asociada", example = "Audífonos", accessMode = Schema.AccessMode.READ_ONLY)
        String clase,
        @Schema(description = "Fecha y hora de creación", accessMode = Schema.AccessMode.READ_ONLY)
        Instant createdAt,
        @Schema(description = "Fecha y hora de última actualización", accessMode = Schema.AccessMode.READ_ONLY)
        Instant updatedAt
) {
}
