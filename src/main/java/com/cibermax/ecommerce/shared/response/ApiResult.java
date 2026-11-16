package com.cibermax.ecommerce.shared.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Estructura estándar de respuesta de la API")
public record ApiResult<T>(
        @Schema(
                description = "Indica si la operación fue exitosa",
                example = "true"
        )
        boolean success,
        @Schema(
                description = "Mensaje descriptivo de la operación",
                example = "Categoría obtenida correctamente"
        )
        String message,
        @Schema(
                description = "Datos devueltos por la operación",
                nullable = true
        )
        T data,
        @Schema(
                description = "Fecha y hora de la respuesta",
                example = "2026-11-15T10:30:00Z",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant timestamp
) {

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(true, message, data, Instant.now());
    }

    public static <T> ApiResult<T> success(T data) {
        return success("Operación realizada correctamente", data);
    }
}
