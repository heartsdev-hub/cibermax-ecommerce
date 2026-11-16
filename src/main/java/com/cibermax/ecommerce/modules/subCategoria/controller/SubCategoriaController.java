package com.cibermax.ecommerce.modules.subCategoria.controller;

import com.cibermax.ecommerce.modules.subCategoria.dto.request.SubCategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.subCategoria.dto.request.SubCategoryUpdateRequestDT;
import com.cibermax.ecommerce.modules.subCategoria.dto.response.SubCategoryResponse;
import com.cibermax.ecommerce.modules.subCategoria.service.SubCategoriaService;
import com.cibermax.ecommerce.shared.response.ApiResult;
import com.cibermax.ecommerce.shared.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Subcategorías",
        description = "API REST para registrar, consultar y actualizar subcategorías relacionadas con categorías"
)
@RestController
@RequestMapping("/api/v1/subcategorias")
public class SubCategoriaController {

    private final SubCategoriaService subCategoriaService;

    public SubCategoriaController(SubCategoriaService subCategoriaService) {
        this.subCategoriaService = subCategoriaService;
    }

    @Operation(
            summary = "Crear subcategoría",
            description = "Registra una subcategoría asociada a una categoría existente. "
                    + "No permite nombres duplicados dentro de la misma categoría."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subcategoría creada correctamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "La categoría padre no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe una subcategoría con ese nombre en la categoría",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ApiResult<SubCategoryResponse>> create(
            @Parameter(description = "Datos de la subcategoría", required = true)
            @Valid @RequestBody SubCategoryCreateRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subCategoriaService.create(request));
    }

    @Operation(
            summary = "Listar subcategorías",
            description = "Obtiene todas las subcategorías registradas con los datos de su categoría padre."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subcategorías obtenidas correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<ApiResult<List<SubCategoryResponse>>> findAll() {
        return ResponseEntity.ok(subCategoriaService.findAll());
    }

    @Operation(
            summary = "Obtener subcategoría por ID",
            description = "Busca una subcategoría mediante su UUID y devuelve también su categoría padre."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subcategoría obtenida correctamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato de UUID inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Subcategoría no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<SubCategoryResponse>> findById(
            @Parameter(description = "UUID de la subcategoría", required = true)
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(subCategoriaService.findById(id));
    }

    @Operation(
            summary = "Actualizar subcategoría",
            description = "Modifica el nombre de una subcategoría existente sin cambiar su categoría padre."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subcategoría actualizada correctamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada o UUID inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Subcategoría no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe otra subcategoría con ese nombre en la categoría",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResult<SubCategoryResponse>> update(
            @Parameter(description = "UUID de la subcategoría", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody SubCategoryUpdateRequestDT request
    ) {
        return ResponseEntity.ok(subCategoriaService.update(id, request));
    }
}
