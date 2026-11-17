package com.cibermax.ecommerce.modules.subclase.controller;

import com.cibermax.ecommerce.modules.subclase.dto.request.SubclaseCreateRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Subclases", description = "API REST para registrar subclases asociadas a una Clasess")
@RestController
@RequestMapping("/api/v1/subclases")
public class SubclaseController {

    private final SubclaseService subclaseService;

    public SubclaseController(SubclaseService subclaseService) {
        this.subclaseService = subclaseService;
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
