package com.cibermax.ecommerce.modules.clasess.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para actualizar una Clasess")
public record ClasessUpdateRequest(
        @Schema(description = "Nuevo nombre de la clase", example = "Audífonos inalámbricos", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El nombre de la clase es obligatorio")
        @Size(max = 100, message = "El nombre de la clase no puede superar los 100 caracteres")
        String nombre
) {
}
