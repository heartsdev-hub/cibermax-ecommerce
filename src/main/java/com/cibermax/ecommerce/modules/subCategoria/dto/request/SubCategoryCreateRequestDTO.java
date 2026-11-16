package com.cibermax.ecommerce.modules.subCategoria.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Datos para crear una subcategoría")
public record SubCategoryCreateRequestDTO(
        @Schema(
                description = "Nombre de la subcategoría",
                example = "Laptops",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El nombre de la subcategoría es obligatorio")
        @Size(max = 100, message = "El nombre de la subcategoría no puede superar los 100 caracteres")
        String nombre,

        @Schema(
                description = "UUID de la categoría padre",
                example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "La categoría es obligatoria")
        UUID categoriaId
) {
}
