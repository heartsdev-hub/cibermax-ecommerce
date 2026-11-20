package com.cibermax.ecommerce.modules.subclase.service;

import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
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
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", "10", null, null);
        Clasess clasess = new Clasess(clasessId, "1010", "Audífonos", categoria, null, null);
        Subclase entity = new Subclase(null, null, "Inalámbricos", clasess, null, null);
        Subclase saved = new Subclase(UUID.randomUUID(), "101010", "Inalámbricos", clasess, null, null);
        SubclaseResponse response = new SubclaseResponse(
                saved.getId(), "101010", "Inalámbricos", "Audio", "Audífonos", null, null
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

        assertThat(entity.getCodigo()).isEqualTo("101010");
        assertThat(result.data()).isEqualTo(response);
        verify(subclaseRepository).saveAndFlush(entity);
    }

    @Test
    void create_incrementsCodeForSameClass() {
        UUID clasessId = UUID.randomUUID();
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", "10", null, null);
        Clasess clasess = new Clasess(clasessId, "1020", "Parlantes", categoria, null, null);
        Subclase previous = new Subclase(UUID.randomUUID(), "102010", "Bluetooth", clasess, null, null);
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

        assertThat(entity.getCodigo()).isEqualTo("102020");
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
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", "10", null, null);
        Clasess clasess = new Clasess(clasessId, "1010", "Audífonos", categoria, null, null);
        when(clasessRepository.findById(clasessId)).thenReturn(Optional.of(clasess));
        when(subclaseRepository.existsByNombreIgnoreCaseAndClasessId("Inalámbricos", clasessId))
                .thenReturn(true);

        assertThatThrownBy(() -> subclaseService.create(
                new SubclaseCreateRequest("Inalámbricos", clasessId)
        )).isInstanceOf(DuplicateResourceException.class);

        verify(subclaseRepository, never()).saveAndFlush(any(Subclase.class));
    }

    @Test
    void findAll_returnsMappedSubclases() {
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", "10", null, null);
        Clasess clasess = new Clasess(UUID.randomUUID(), "1010", "Audífonos", categoria, null, null);
        Subclase entity = new Subclase(UUID.randomUUID(), "101010", "Inalámbricos", clasess, null, null);
        SubclaseResponse response = new SubclaseResponse(
                entity.getId(), "101010", "Inalámbricos", "Audio", "Audífonos", null, null
        );

        when(subclaseRepository.findAllByOrderByCodigoAsc()).thenReturn(List.of(entity));
        when(subclaseMapper.toResponse(entity)).thenReturn(response);

        ApiResult<List<SubclaseResponse>> result = subclaseService.findAll();

        assertThat(result.data()).containsExactly(response);
    }

    @Test
    void findById_returnsMappedSubclase() {
        UUID id = UUID.randomUUID();
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", "10", null, null);
        Clasess clasess = new Clasess(UUID.randomUUID(), "1010", "Audífonos", categoria, null, null);
        Subclase entity = new Subclase(id, "101010", "Inalámbricos", clasess, null, null);
        SubclaseResponse response = new SubclaseResponse(
                id, "101010", "Inalámbricos", "Audio", "Audífonos", null, null
        );

        when(subclaseRepository.findById(id)).thenReturn(Optional.of(entity));
        when(subclaseMapper.toResponse(entity)).thenReturn(response);

        ApiResult<SubclaseResponse> result = subclaseService.findById(id);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).isEqualTo("Subclase obtenida correctamente");
        assertThat(result.data()).isEqualTo(response);
        verify(subclaseMapper).toResponse(entity);
    }

    @Test
    void findById_rejectsMissingSubclase() {
        UUID id = UUID.randomUUID();
        when(subclaseRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subclaseService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(subclaseMapper, never()).toResponse(any(Subclase.class));
    }

    @Test
    void update_changesNameAndKeepsCodeAndParent() {
        UUID id = UUID.randomUUID();
        UUID clasessId = UUID.randomUUID();
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", "10", null, null);
        Clasess clasess = new Clasess(clasessId, "1010", "Audífonos", categoria, null, null);
        Subclase entity = new Subclase(id, "101010", "Inalámbricos", clasess, null, null);
        SubclaseResponse response = new SubclaseResponse(
                id, "101010", "Audífonos inalámbricos", "Audio", "Audífonos", null, null
        );
        SubclaseUpdateRequest request = new SubclaseUpdateRequest("Audífonos inalámbricos");

        when(subclaseRepository.findById(id)).thenReturn(Optional.of(entity));
        when(subclaseRepository.existsByNombreIgnoreCaseAndClasessIdAndIdNot(
                request.nombre(), clasessId, id
        )).thenReturn(false);
        when(subclaseRepository.saveAndFlush(entity)).thenReturn(entity);
        when(subclaseMapper.toResponse(entity)).thenReturn(response);

        ApiResult<SubclaseResponse> result = subclaseService.update(id, request);

        assertThat(result.data()).isEqualTo(response);
        assertThat(entity.getCodigo()).isEqualTo("101010");
        assertThat(entity.getClasess()).isSameAs(clasess);
        verify(subclaseMapper).updateEntity(request, entity);
        verify(subclaseRepository).saveAndFlush(entity);
    }

    @Test
    void update_rejectsMissingSubclase() {
        UUID id = UUID.randomUUID();
        when(subclaseRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subclaseService.update(
                id, new SubclaseUpdateRequest("Audífonos inalámbricos")
        )).isInstanceOf(ResourceNotFoundException.class);

        verify(subclaseRepository, never()).saveAndFlush(any(Subclase.class));
    }

    @Test
    void update_rejectsDuplicateNameInSameClass() {
        UUID id = UUID.randomUUID();
        UUID clasessId = UUID.randomUUID();
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", "10", null, null);
        Clasess clasess = new Clasess(clasessId, "1010", "Audífonos", categoria, null, null);
        Subclase entity = new Subclase(id, "101010", "Inalámbricos", clasess, null, null);
        SubclaseUpdateRequest request = new SubclaseUpdateRequest("Bluetooth");

        when(subclaseRepository.findById(id)).thenReturn(Optional.of(entity));
        when(subclaseRepository.existsByNombreIgnoreCaseAndClasessIdAndIdNot(
                request.nombre(), clasessId, id
        )).thenReturn(true);

        assertThatThrownBy(() -> subclaseService.update(id, request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(subclaseMapper, never()).updateEntity(request, entity);
        verify(subclaseRepository, never()).saveAndFlush(any(Subclase.class));
    }
}
