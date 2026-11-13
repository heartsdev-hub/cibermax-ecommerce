package com.cibermax.ecommerce.modules.categoria.controller;

import com.cibermax.ecommerce.modules.categoria.dto.request.CategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.categoria.dto.response.CategoryResponse;
import com.cibermax.ecommerce.modules.categoria.service.CategoriaService;
import com.cibermax.ecommerce.shared.response.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<ApiResult<List<CategoryResponse>>> findAll() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<CategoryResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoriaService.findById(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResult<List<CategoryResponse>>> searchByName(
            @RequestParam String nombre
    ) {
        return ResponseEntity.ok(categoriaService.searchByName(nombre));
    }

    @PostMapping
    public ResponseEntity<ApiResult<CategoryResponse>> create(@Valid @RequestBody CategoryCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.create(request));
    }
}
