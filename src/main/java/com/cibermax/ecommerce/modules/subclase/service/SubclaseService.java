package com.cibermax.ecommerce.modules.subclase.service;

import com.cibermax.ecommerce.modules.clasess.entity.Clasess;
import com.cibermax.ecommerce.modules.clasess.repository.ClasessRepository;
import com.cibermax.ecommerce.modules.subclase.dto.request.SubclaseCreateRequest;
import com.cibermax.ecommerce.modules.subclase.dto.request.SubclaseUpdateRequest;
import com.cibermax.ecommerce.modules.subclase.dto.response.SubclaseResponse;
import com.cibermax.ecommerce.modules.subclase.entity.Subclase;
import com.cibermax.ecommerce.modules.subclase.mapper.SubclaseMapper;
import com.cibermax.ecommerce.modules.subclase.repository.SubclaseRepository;
import com.cibermax.ecommerce.shared.exception.DuplicateResourceException;
import com.cibermax.ecommerce.shared.exception.ResourceNotFoundException;
import com.cibermax.ecommerce.shared.response.ApiResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SubclaseService {

    private static final int CODE_STEP = 10;

    private final SubclaseRepository subclaseRepository;
    private final ClasessRepository clasessRepository;
    private final SubclaseMapper subclaseMapper;

    public SubclaseService(
            SubclaseRepository subclaseRepository,
            ClasessRepository clasessRepository,
            SubclaseMapper subclaseMapper
    ) {
        this.subclaseRepository = subclaseRepository;
        this.clasessRepository = clasessRepository;
        this.subclaseMapper = subclaseMapper;
    }

    @Transactional
    public ApiResult<SubclaseResponse> create(SubclaseCreateRequest request) {
        Clasess clasess = clasessRepository.findById(request.clasessId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la Clasess con id: " + request.clasessId()
                ));

        if (subclaseRepository.existsByNombreIgnoreCaseAndClasessId(
                request.nombre(), request.clasessId()
        )) {
            throw new DuplicateResourceException(
                    "Ya existe una subclase con el nombre indicado en la Clasess seleccionada"
            );
        }

        Subclase subclase = subclaseMapper.toEntity(request, clasess);
        subclase.setCodigo(nextSubclaseCode(clasess));
        Subclase saved = subclaseRepository.saveAndFlush(subclase);

        return ApiResult.success(
                "Subclase creada correctamente",
                subclaseMapper.toResponse(saved)
        );
    }

    @Transactional(readOnly = true)
    public ApiResult<List<SubclaseResponse>> findAll() {
        List<SubclaseResponse> subclases = subclaseRepository.findAllByOrderByCodigoAsc()
                .stream()
                .map(subclaseMapper::toResponse)
                .toList();

        return ApiResult.success("Subclases obtenidas correctamente", subclases);
    }

    @Transactional(readOnly = true)
    public ApiResult<SubclaseResponse> findById(UUID id) {
        Subclase subclase = subclaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la subclase con id: " + id
                ));

        return ApiResult.success(
                "Subclase obtenida correctamente",
                subclaseMapper.toResponse(subclase)
        );
    }

    @Transactional
    public ApiResult<SubclaseResponse> update(UUID id, SubclaseUpdateRequest request) {
        Subclase subclase = subclaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la subclase con id: " + id
                ));

        if (subclaseRepository.existsByNombreIgnoreCaseAndClasessIdAndIdNot(
                request.nombre(), subclase.getClasess().getId(), id
        )) {
            throw new DuplicateResourceException(
                    "Ya existe una subclase con el nombre indicado en la Clasess seleccionada"
            );
        }

        subclaseMapper.updateEntity(request, subclase);
        Subclase updated = subclaseRepository.saveAndFlush(subclase);

        return ApiResult.success(
                "Subclase actualizada correctamente",
                subclaseMapper.toResponse(updated)
        );
    }

    private String nextSubclaseCode(Clasess clasess) {
        List<Subclase> subclases = subclaseRepository.findByClasessIdOrderByCodigoAsc(clasess.getId());
        String classCode = String.valueOf(clasess.getCodigo());
        int nextSequence = subclases.stream()
                .map(Subclase::getCodigo)
                .mapToInt(code -> extractSequence(code, classCode))
                .max()
                .orElse(0) + CODE_STEP;

        return classCode + nextSequence;
    }

    private int extractSequence(String code, String classCode) {
        if (code == null || code.isBlank()) {
            return 0;
        }

        String sequence = code.startsWith(classCode)
                ? code.substring(classCode.length())
                : code.substring(Math.max(0, code.length() - 2));

        try {
            return Integer.parseInt(sequence);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}
