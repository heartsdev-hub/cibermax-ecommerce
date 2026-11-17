package com.cibermax.ecommerce.modules.subclase.service;

import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubclaseServiceTest {

    @Mock
    private SubclaseRepository subclaseRepository;

    @Mock
    private ClasessRepository clasessRepository;

    @Mock
    private SubclaseMapper subclaseMapper;

    @InjectMocks
    private SubclaseService subclaseService;

    @Test
    void create_generatesFirstCodeFromClassCode() {
        UUID clasessId = UUID.randomUUID();
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", 10, null, null);
        Clasess clasess = new Clasess(clasessId, 1010, "Audífonos", categoria, null, null);
        Subclase entity = new Subclase(null, null, "Inalámbricos", clasess, null, null);
        Subclase saved = new Subclase(UUID.randomUUID(), 101010, "Inalámbricos", clasess, null, null);
        SubclaseResponse response = new SubclaseResponse(
                saved.getId(), 101010, "Inalámbricos", "Audio", "Audífonos", null, null
        );
        SubclaseCreateRequest request = new SubclaseCreateRequest("Inalámbricos", clasessId);

        when(clasessRepository.findById(clasessId)).thenReturn(Optional.of(clasess));
        when(subclaseRepository.existsByNombreIgnoreCaseAndClasessId("Inalámbricos", clasessId))
                .thenReturn(false);
        when(subclaseRepository.findByClasessIdOrderByCodigoAsc(clasessId))
                .thenReturn(List.of());
        when(subclaseMapper.toEntity(request, clasess)).thenReturn(entity);
        when(subclaseRepository.saveAndFlush(entity)).thenReturn(saved);
        when(subclaseMapper.toResponse(saved)).thenReturn(response);

        ApiResult<SubclaseResponse> result = subclaseService.create(request);

        assertThat(entity.getCodigo()).isEqualTo(101010);
        assertThat(result.data()).isEqualTo(response);
        verify(subclaseRepository).saveAndFlush(entity);
    }

    @Test
    void create_incrementsCodeForSameClass() {
        UUID clasessId = UUID.randomUUID();
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", 10, null, null);
        Clasess clasess = new Clasess(clasessId, 1020, "Parlantes", categoria, null, null);
        Subclase previous = new Subclase(UUID.randomUUID(), 102010, "Bluetooth", clasess, null, null);
        Subclase entity = new Subclase(null, null, "Portátiles", clasess, null, null);
        SubclaseCreateRequest request = new SubclaseCreateRequest("Portátiles", clasessId);

        when(clasessRepository.findById(clasessId)).thenReturn(Optional.of(clasess));
        when(subclaseRepository.existsByNombreIgnoreCaseAndClasessId("Portátiles", clasessId))
                .thenReturn(false);
        when(subclaseRepository.findByClasessIdOrderByCodigoAsc(clasessId))
                .thenReturn(List.of(previous));
        when(subclaseMapper.toEntity(request, clasess)).thenReturn(entity);
        when(subclaseRepository.saveAndFlush(entity)).thenReturn(entity);
        when(subclaseMapper.toResponse(entity)).thenReturn(null);

        subclaseService.create(request);

        assertThat(entity.getCodigo()).isEqualTo(102020);
    }

    @Test
    void create_rejectsMissingClass() {
        UUID clasessId = UUID.randomUUID();
        when(clasessRepository.findById(clasessId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subclaseService.create(
                new SubclaseCreateRequest("Inalámbricos", clasessId)
        )).isInstanceOf(ResourceNotFoundException.class);

        verify(subclaseRepository, never()).saveAndFlush(any(Subclase.class));
    }

    @Test
    void create_rejectsDuplicateName() {
        UUID clasessId = UUID.randomUUID();
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", 10, null, null);
        Clasess clasess = new Clasess(clasessId, 1010, "Audífonos", categoria, null, null);
        when(clasessRepository.findById(clasessId)).thenReturn(Optional.of(clasess));
        when(subclaseRepository.existsByNombreIgnoreCaseAndClasessId("Inalámbricos", clasessId))
                .thenReturn(true);

        assertThatThrownBy(() -> subclaseService.create(
                new SubclaseCreateRequest("Inalámbricos", clasessId)
        )).isInstanceOf(DuplicateResourceException.class);

        verify(subclaseRepository, never()).saveAndFlush(any(Subclase.class));
    }
}
