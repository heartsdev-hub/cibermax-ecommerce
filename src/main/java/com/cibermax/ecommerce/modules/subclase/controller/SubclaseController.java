package com.cibermax.ecommerce.modules.subclase.controller;

import com.cibermax.ecommerce.modules.subclase.dto.request.SubclaseCreateRequest;
import com.cibermax.ecommerce.modules.subclase.dto.request.SubclaseUpdateRequest;
import com.cibermax.ecommerce.modules.subclase.dto.response.SubclaseResponse;
import com.cibermax.ecommerce.modules.subclase.service.SubclaseService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.UUID;

@Tag(name = "Subclases", description = "API REST para registrar subclases asociadas a una Clasess")
@RestController
@RequestMapping("/api/v1/subclases")
public class SubclaseController {

    private final SubclaseService subclaseService;

    public SubclaseController(SubclaseService subclaseService) {
        this.subclaseService = subclaseService;
    }

    @Operation(
            summary = "Listar subclases",
            description = "Obtiene todas las subclases ordenadas por código ascendente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subclases obtenidas correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<ApiResult<List<SubclaseResponse>>> findAll() {
        return ResponseEntity.ok(subclaseService.findAll());
    }

    @Operation(
            summary = "Obtener subclase por ID",
            description = "Obtiene una subclase mediante su UUID, incluyendo su categoría y Clasess relacionadas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subclase obtenida correctamente"),
            @ApiResponse(responseCode = "400", description = "Formato de UUID inválido"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Subclase no encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<SubclaseResponse>> findById(
            @Parameter(description = "UUID de la subclase", required = true)
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(subclaseService.findById(id));
    }

    @Operation(
            summary = "Actualizar subclase",
            description = "Actualiza únicamente el nombre de una subclase. El código y la Clasess padre permanecen inmutables."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subclase actualizada correctamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Subclase no encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe una subclase con ese nombre en la Clasess",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResult<SubclaseResponse>> update(
            @Parameter(description = "UUID de la subclase", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody SubclaseUpdateRequest request
    ) {
        return ResponseEntity.ok(subclaseService.update(id, request));
    }

    @Operation(
            summary = "Crear subclase",
            description = "Registra una subclase y genera su código usando el código de la Clasess padre."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subclase creada correctamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "La Clasess no existe",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe una subclase con ese nombre en la Clasess",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiResult<SubclaseResponse>> create(
            @Valid @RequestBody SubclaseCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subclaseService.create(request));
    }
}
