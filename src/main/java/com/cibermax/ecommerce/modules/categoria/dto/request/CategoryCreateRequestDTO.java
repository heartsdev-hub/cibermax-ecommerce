package com.cibermax.ecommerce.modules.categoria.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para crear una categoría")
public record CategoryCreateRequestDTO(
        @Schema(
                description = "Nombre de la categoría",
                example = "Almacenamiento",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El nombre de la categoría es obligatorio")
        @Size(max = 100, message = "El nombre de la categoría no puede superar los 100 caracteres")
        String nombre
) {
}
