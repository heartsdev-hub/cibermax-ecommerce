package com.cibermax.ecommerce.shared.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Estructura estándar de las respuestas de error")
public record ErrorResponse(
        @Schema(
                description = "Fecha y hora en que ocurrió el error",
                example = "2026-11-15T15:30:00Z"
        )
        Instant timestamp,
        @Schema(
                description = "Código HTTP del error",
                example = "404"
        )
        int status,
        @Schema(
                description = "Nombre del error HTTP",
                example = "NOT FOUND"
        )
        String error,
        @Schema(
                description = "Mensaje que explica el error",
                example = "No se encontró la categoría con el ID indicado"
        )
        String message,
        @Schema(
                description = "Ruta donde ocurrió el error",
                example = "/api/v1/categorias/550e8400-e29b-41d4-a716-446655440000"
        )
        String path
) {
}
