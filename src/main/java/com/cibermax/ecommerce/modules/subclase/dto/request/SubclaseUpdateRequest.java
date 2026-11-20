package com.cibermax.ecommerce.modules.subclase.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para actualizar una subclase")
public record SubclaseUpdateRequest(
        @Schema(
                description = "Nuevo nombre de la subclase",
                example = "Audífonos inalámbricos",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El nombre de la subclase es obligatorio")
        @Size(max = 100, message = "El nombre de la subclase no puede superar los 100 caracteres")
        String nombre
) {
}
