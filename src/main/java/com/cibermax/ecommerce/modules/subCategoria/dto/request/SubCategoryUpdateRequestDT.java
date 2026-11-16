package com.cibermax.ecommerce.modules.subCategoria.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para actualizar una subcategoría")
public record SubCategoryUpdateRequestDT(
        @Schema(
                description = "Nuevo nombre de la subcategoría",
                example = "Laptops profesionales",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El nombre de la subcategoría es obligatorio")
        @Size(max = 100, message = "El nombre de la subcategoría no puede superar los 100 caracteres")
        String nombre
) {
}
