package com.cibermax.ecommerce.modules.subclase.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Datos para registrar una subclase asociada a una Clasess")
public record SubclaseCreateRequest(
        @Schema(description = "Nombre de la subclase", example = "Audífonos inalámbricos", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El nombre de la subclase es obligatorio")
        @Size(max = 100, message = "El nombre de la subclase no puede superar los 100 caracteres")
        String nombre,
        @Schema(description = "UUID de la Clasess padre", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "La Clasess es obligatoria")
        UUID clasessId
) {
}
