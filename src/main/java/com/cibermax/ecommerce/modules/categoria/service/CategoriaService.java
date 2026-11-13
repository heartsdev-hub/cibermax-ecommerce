package com.cibermax.ecommerce.modules.categoria.service;

import com.cibermax.ecommerce.modules.categoria.dto.request.CategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.categoria.dto.response.CategoryResponse;
import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.categoria.mapper.CategoriaMapper;
import com.cibermax.ecommerce.modules.categoria.repository.CategoriaRepository;
import com.cibermax.ecommerce.shared.exception.DuplicateResourceException;
import com.cibermax.ecommerce.shared.exception.ResourceNotFoundException;
import com.cibermax.ecommerce.shared.response.ApiResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Transactional(readOnly = true)
    public ApiResult<List<CategoryResponse>> findAll() {
        List<CategoryResponse> categories = categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();

        return ApiResult.success("Categorías obtenidas correctamente", categories);
    }

    @Transactional(readOnly = true)
    public ApiResult<CategoryResponse> findById(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con id: " + id));

        return ApiResult.success(
                "Categoría obtenida correctamente",
                categoriaMapper.toResponse(categoria)
        );
    }

    @Transactional(readOnly = true)
    public ApiResult<List<CategoryResponse>> searchByName(String nombre) {
        List<CategoryResponse> categories = categoriaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();

        return ApiResult.success("Búsqueda de categorías realizada correctamente", categories);
    }

    @Transactional
    public ApiResult<CategoryResponse> create(CategoryCreateRequestDTO request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new DuplicateResourceException("Ya existe una categoría con el nombre indicado");
        }
        Categoria categoria = categoriaMapper.toEntity(request);
        Categoria saved = categoriaRepository.save(categoria);
        return ApiResult.success("Categoría creada correctamente", categoriaMapper.toResponse(saved));
    }
}
