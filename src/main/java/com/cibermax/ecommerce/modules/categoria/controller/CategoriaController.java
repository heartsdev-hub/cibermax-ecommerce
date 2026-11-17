package com.cibermax.ecommerce.modules.categoria.controller;

import com.cibermax.ecommerce.modules.categoria.dto.request.CategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.categoria.dto.request.CategoryUpdateRequestDT;
import com.cibermax.ecommerce.modules.categoria.dto.response.CategoryResponse;
import com.cibermax.ecommerce.modules.categoria.service.CategoriaService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
@Tag(
        name = "Categorías",
        description = "API REST para la gestión de categorías del ecommerce"
)
@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(
            summary = "Listar categorias",
            description = "Obtiene todos las categorias registradas"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria obtenidas correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<ApiResult<List<CategoryResponse>>> findAll() {
        return ResponseEntity.ok(categoriaService.findAll());
    }
    @Operation(
            summary = "Obtener categoria por ID",
            description = "Busca una categoria mediante su UUID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria obtenida correctamente"),
            @ApiResponse(responseCode = "400", description = "Formato de UUID inválido."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoria no encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<CategoryResponse>> findById(@Parameter(description = "UUID de la categoria", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(categoriaService.findById(id));
    }
    @Operation(
            summary = "Buscar categorias por nombre",
            description = "Busca categorías que contengan el texto indicado, sin distinguir mayúsculas y minúsculas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetro de búsqueda inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/buscar")
    public ResponseEntity<ApiResult<List<CategoryResponse>>> searchByName(
            @Parameter(description = "Nombre o parte del nombre", example = "SSD", required = true)
            @RequestParam String nombre
    ) {
        return ResponseEntity.ok(categoriaService.searchByName(nombre));
    }
    @Operation(
            summary = "Actualizar categoria",
            description = "Modifica los datos de una categoria existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente"),
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
                    description = "Categoria no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe otra categoria con ese nombre",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResult<CategoryResponse>> update(
            @Parameter(description = "UUID de la categoría", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody CategoryUpdateRequestDT request
    ) {
        return ResponseEntity.ok(categoriaService.update(id, request));
    }
    @Operation(
            summary = "Crear categoría",
            description = "Registra una nueva categoría y asigna automáticamente el siguiente código múltiplo de 10."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoría creada correctamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe una categoría con ese nombre",
                    content = @Content(
                            mediaType = "appication/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ApiResult<CategoryResponse>> create(@Valid @RequestBody CategoryCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.create(request));
    }
}
