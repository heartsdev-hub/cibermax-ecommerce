package com.cibermax.ecommerce.modules.clasess.service;

import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.categoria.repository.CategoriaRepository;
import com.cibermax.ecommerce.modules.clasess.dto.request.ClasessCreateRequest;
import com.cibermax.ecommerce.modules.clasess.dto.request.ClasessUpdateRequest;
import com.cibermax.ecommerce.modules.clasess.dto.response.ClasessResponse;
import com.cibermax.ecommerce.modules.clasess.entity.Clasess;
import com.cibermax.ecommerce.modules.clasess.mapper.ClasessMapper;
import com.cibermax.ecommerce.modules.clasess.repository.ClasessRepository;
import com.cibermax.ecommerce.shared.exception.DuplicateResourceException;
import com.cibermax.ecommerce.shared.exception.ResourceNotFoundException;
import com.cibermax.ecommerce.shared.response.ApiResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClasessService {

    private static final int CODE_STEP = 10;
    private static final int CODE_BASE = 100;

    private final ClasessRepository clasessRepository;
    private final CategoriaRepository categoriaRepository;
    private final ClasessMapper clasessMapper;

    public ClasessService(
            ClasessRepository clasessRepository,
            CategoriaRepository categoriaRepository,
            ClasessMapper clasessMapper
    ) {
        this.clasessRepository = clasessRepository;
        this.categoriaRepository = categoriaRepository;
        this.clasessMapper = clasessMapper;
    }

    @Transactional
    public ApiResult<ClasessResponse> create(ClasessCreateRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la categoría con id: " + request.categoriaId()
                ));

        if (clasessRepository.existsByNombreIgnoreCaseAndCategoriaId(
                request.nombre(), request.categoriaId()
        )) {
            throw new DuplicateResourceException(
                    "Ya existe una clase con el nombre indicado en la categoría seleccionada"
            );
        }

        Clasess clase = clasessMapper.toEntity(request, categoria);
        clase.setCodigo(nextClassCode(categoria));
        Clasess saved = clasessRepository.saveAndFlush(clase);

        return ApiResult.success(
                "Clasess creada correctamente",
                clasessMapper.toResponse(saved)
        );
    }

    @Transactional(readOnly = true)
    public ApiResult<List<ClasessResponse>> findAll() {
        List<ClasessResponse> classes = clasessRepository.findAllByOrderByCodigoAsc()
                .stream()
                .map(clasessMapper::toResponse)
                .toList();

        return ApiResult.success("Clasess obtenidas correctamente", classes);
    }

    @Transactional(readOnly = true)
    public ApiResult<ClasessResponse> findById(java.util.UUID id) {
        Clasess clase = clasessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la Clasess con id: " + id
                ));

        return ApiResult.success(
                "Clasess obtenida correctamente",
                clasessMapper.toResponse(clase)
        );
    }

    @Transactional
    public ApiResult<ClasessResponse> update(java.util.UUID id, ClasessUpdateRequest request) {
        Clasess clase = clasessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la Clasess con id: " + id
                ));

        if (clasessRepository.existsByNombreIgnoreCaseAndCategoriaIdAndIdNot(
                request.nombre(), clase.getCategoria().getId(), id
        )) {
            throw new DuplicateResourceException(
                    "Ya existe una clase con el nombre indicado en la categoría seleccionada"
            );
        }

        clasessMapper.updateEntity(request, clase);
        Clasess updated = clasessRepository.saveAndFlush(clase);

        return ApiResult.success(
                "Clasess actualizada correctamente",
                clasessMapper.toResponse(updated)
        );
    }

    private int nextClassCode(Categoria categoria) {
        List<Clasess> classes = clasessRepository.findByCategoriaIdOrderByCodigoAsc(categoria.getId());
        int nextSequence = classes.stream()
                .map(Clasess::getCodigo)
                .mapToInt(this::extractSequence)
                .max()
                .orElse(0) + CODE_STEP;

        return categoria.getCodigo() * CODE_BASE + nextSequence;
    }

    private int extractSequence(Integer code) {
        if (code == null) {
            return 0;
        }
        return Math.abs(code) % CODE_BASE;
    }
}
