package com.cibermax.ecommerce.modules.subCategoria.service;

import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.categoria.repository.CategoriaRepository;
import com.cibermax.ecommerce.modules.subCategoria.dto.request.SubCategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.subCategoria.dto.response.SubCategoryResponse;
import com.cibermax.ecommerce.modules.subCategoria.entity.SubCategoria;
import com.cibermax.ecommerce.modules.subCategoria.mapper.SubCategoriaMapper;
import com.cibermax.ecommerce.modules.subCategoria.repository.SubCategoriaRepository;
import com.cibermax.ecommerce.shared.exception.DuplicateResourceException;
import com.cibermax.ecommerce.shared.exception.ResourceNotFoundException;
import com.cibermax.ecommerce.shared.response.ApiResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SubCategoriaService {

    private final SubCategoriaRepository subCategoriaRepository;
    private final CategoriaRepository categoriaRepository;
    private final SubCategoriaMapper subCategoriaMapper;

    public SubCategoriaService(
            SubCategoriaRepository subCategoriaRepository,
            CategoriaRepository categoriaRepository,
            SubCategoriaMapper subCategoriaMapper
    ) {
        this.subCategoriaRepository = subCategoriaRepository;
        this.categoriaRepository = categoriaRepository;
        this.subCategoriaMapper = subCategoriaMapper;
    }

    @Transactional
    public ApiResult<SubCategoryResponse> create(SubCategoryCreateRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la categoría con id: " + request.categoriaId()
                ));

        if (subCategoriaRepository.existsByNombreIgnoreCaseAndCategoriaId(
                request.nombre(), request.categoriaId()
        )) {
            throw new DuplicateResourceException(
                    "Ya existe una subcategoría con el nombre indicado en la categoría seleccionada"
            );
        }

        SubCategoria subCategoria = subCategoriaMapper.toEntity(request, categoria);
        SubCategoria saved = subCategoriaRepository.saveAndFlush(subCategoria);

        return ApiResult.success(
                "Subcategoría creada correctamente",
                subCategoriaMapper.toResponse(saved)
        );
    }

    @Transactional(readOnly = true)
    public ApiResult<List<SubCategoryResponse>> findAll() {
        List<SubCategoryResponse> subCategorias = subCategoriaRepository.findAll()
                .stream()
                .map(subCategoriaMapper::toResponse)
                .toList();

        return ApiResult.success("Subcategorías obtenidas correctamente", subCategorias);
    }

    @Transactional(readOnly = true)
    public ApiResult<SubCategoryResponse> findById(UUID id) {
        SubCategoria subCategoria = subCategoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la subcategoría con id: " + id
                ));

        return ApiResult.success(
                "Subcategoría obtenida correctamente",
                subCategoriaMapper.toResponse(subCategoria)
        );
    }
}
