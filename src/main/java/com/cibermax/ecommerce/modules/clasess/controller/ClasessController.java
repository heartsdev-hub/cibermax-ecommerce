package com.cibermax.ecommerce.modules.clasess.controller;

import com.cibermax.ecommerce.modules.clasess.dto.request.ClasessCreateRequest;
import com.cibermax.ecommerce.modules.clasess.dto.request.ClasessUpdateRequest;
import com.cibermax.ecommerce.modules.clasess.dto.response.ClasessResponse;
import com.cibermax.ecommerce.modules.clasess.service.ClasessService;
import com.cibermax.ecommerce.shared.response.ApiResult;
import com.cibermax.ecommerce.shared.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Clasess", description = "API REST para gestionar clasess asociadas a categorías")
@RestController
@RequestMapping("/api/v1/clasess")
public class ClasessController {

    private final ClasessService clasessService;

    public ClasessController(ClasessService clasessService) {
        this.clasessService = clasessService;
    }

    @Operation(
            summary = "Listar Clasess",
            description = "Obtiene todas las clasess ordenadas por código ascendente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clasess obtenidas correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<ApiResult<List<ClasessResponse>>> findAll() {
        return ResponseEntity.ok(clasessService.findAll());
    }

    @Operation(
            summary = "Obtener Clasess por ID",
            description = "Obtiene una Clasess mediante su UUID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clasess obtenida correctamente"),
            @ApiResponse(responseCode = "400", description = "Formato de UUID inválido"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Clasess no encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<ClasessResponse>> findById(
            @Parameter(description = "UUID de la Clasess", required = true)
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(clasessService.findById(id));
    }

    @Operation(
            summary = "Actualizar Clasess",
            description = "Actualiza únicamente el nombre. El código y la categoría permanecen inmutables."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clasess actualizada correctamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Clasess no encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe una clase con ese nombre en la categoría",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResult<ClasessResponse>> update(
            @Parameter(description = "UUID de la Clasess", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody ClasessUpdateRequest request
    ) {
        return ResponseEntity.ok(clasessService.update(id, request));
    }

    @Operation(
            summary = "Crear Clasess",
            description = "Registra una clase y genera su código usando el código de la categoría padre."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Clasess creada correctamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "La categoría no existe",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe una clase con ese nombre en la categoría",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiResult<ClasessResponse>> create(
            @Valid @RequestBody ClasessCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clasessService.create(request));
    }
}
