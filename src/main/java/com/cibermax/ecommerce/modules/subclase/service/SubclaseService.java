package com.cibermax.ecommerce.modules.subclase.service;

import com.cibermax.ecommerce.modules.clasess.entity.Clasess;
import com.cibermax.ecommerce.modules.clasess.repository.ClasessRepository;
import com.cibermax.ecommerce.modules.subclase.dto.request.SubclaseCreateRequest;
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

@Service
public class SubclaseService {

    private static final int CODE_STEP = 10;
    private static final int CODE_BASE = 100;

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

    private int nextSubclaseCode(Clasess clasess) {
        List<Subclase> subclases = subclaseRepository.findByClasessIdOrderByCodigoAsc(clasess.getId());
        int nextSequence = subclases.stream()
                .map(Subclase::getCodigo)
                .mapToInt(this::extractSequence)
                .max()
                .orElse(0) + CODE_STEP;

        return clasess.getCodigo() * CODE_BASE + nextSequence;
    }

    private int extractSequence(Integer code) {
        if (code == null) {
            return 0;
        }
        return Math.abs(code) % CODE_BASE;
    }
}
