package com.cibermax.ecommerce.modules.clasess.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Datos para registrar una clase asociada a una categoría")
public record ClasessCreateRequest(
        @Schema(description = "Nombre de la clase", example = "Audífonos", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El nombre de la clase es obligatorio")
        @Size(max = 100, message = "El nombre de la clase no puede superar los 100 caracteres")
        String nombre,
        @Schema(description = "UUID de la categoría padre", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "La categoría es obligatoria")
        UUID categoriaId
) {
}
