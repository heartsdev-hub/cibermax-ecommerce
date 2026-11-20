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
class ClasessServiceTest {

    @Mock
    private ClasessRepository clasessRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ClasessMapper clasessMapper;

    @InjectMocks
    private ClasessService clasessService;

    @Test
    void create_generatesFirstCodeFromCategoryCode() {
        UUID categoriaId = UUID.randomUUID();
        Categoria categoria = new Categoria(categoriaId, "Audio", "10", null, null);
        Clasess entity = new Clasess(null, null, "Audífonos", categoria, null, null);
        Clasess saved = new Clasess(UUID.randomUUID(), "1010", "Audífonos", categoria, null, null);
        ClasessResponse response = new ClasessResponse(
                saved.getId(), "1010", "Audífonos", "Audio", null, null
        );
        ClasessCreateRequest request = new ClasessCreateRequest("Audífonos", categoriaId);

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(clasessRepository.existsByNombreIgnoreCaseAndCategoriaId("Audífonos", categoriaId))
                .thenReturn(false);
        when(clasessRepository.findByCategoriaIdOrderByCodigoAsc(categoriaId))
                .thenReturn(java.util.List.of());
        when(clasessMapper.toEntity(request, categoria)).thenReturn(entity);
        when(clasessRepository.saveAndFlush(entity)).thenReturn(saved);
        when(clasessMapper.toResponse(saved)).thenReturn(response);

        ApiResult<ClasessResponse> result = clasessService.create(request);

        assertThat(entity.getCodigo()).isEqualTo("1010");
        assertThat(result.data()).isEqualTo(response);
        verify(clasessRepository).saveAndFlush(entity);
    }

    @Test
    void create_incrementsCodeForSameCategory() {
        UUID categoriaId = UUID.randomUUID();
        Categoria categoria = new Categoria(categoriaId, "Audio", "10", null, null);
        Clasess previous = new Clasess(UUID.randomUUID(), "1050", "Audífonos", categoria, null, null);
        Clasess entity = new Clasess(null, null, "Parlantes", categoria, null, null);
        ClasessCreateRequest request = new ClasessCreateRequest("Parlantes", categoriaId);
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(clasessRepository.existsByNombreIgnoreCaseAndCategoriaId("Parlantes", categoriaId))
                .thenReturn(false);
        when(clasessRepository.findByCategoriaIdOrderByCodigoAsc(categoriaId))
                .thenReturn(java.util.List.of(previous));
        when(clasessMapper.toEntity(request, categoria)).thenReturn(entity);
        when(clasessRepository.saveAndFlush(entity)).thenReturn(entity);
        when(clasessMapper.toResponse(entity)).thenReturn(null);

        clasessService.create(request);

        assertThat(entity.getCodigo()).isEqualTo("1060");
    }

    @Test
    void create_startsSequenceAtTenForAnotherCategory() {
        UUID categoriaId = UUID.randomUUID();
        Categoria categoria = new Categoria(categoriaId, "Video", "20", null, null);
        Clasess entity = new Clasess(null, null, "Televisores", categoria, null, null);
        ClasessCreateRequest request = new ClasessCreateRequest("Televisores", categoriaId);

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(clasessRepository.existsByNombreIgnoreCaseAndCategoriaId("Televisores", categoriaId))
                .thenReturn(false);
        when(clasessRepository.findByCategoriaIdOrderByCodigoAsc(categoriaId))
                .thenReturn(List.of());
        when(clasessMapper.toEntity(request, categoria)).thenReturn(entity);
        when(clasessRepository.saveAndFlush(entity)).thenReturn(entity);
        when(clasessMapper.toResponse(entity)).thenReturn(null);

        clasessService.create(request);

        assertThat(entity.getCodigo()).isEqualTo("2010");
    }

    @Test
    void findAll_returnsMappedClasses() {
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", "10", null, null);
        Clasess entity = new Clasess(UUID.randomUUID(), "1010", "Audífonos", categoria, null, null);
        ClasessResponse response = new ClasessResponse(
                entity.getId(), "1010", "Audífonos", "Audio", null, null
        );

        when(clasessRepository.findAllByOrderByCodigoAsc()).thenReturn(List.of(entity));
        when(clasessMapper.toResponse(entity)).thenReturn(response);

        ApiResult<List<ClasessResponse>> result = clasessService.findAll();

        assertThat(result.data()).containsExactly(response);
    }

    @Test
    void findById_returnsMappedClass() {
        UUID id = UUID.randomUUID();
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", "10", null, null);
        Clasess entity = new Clasess(id, "1010", "Audífonos", categoria, null, null);
        ClasessResponse response = new ClasessResponse(id, "1010", "Audífonos", "Audio", null, null);

        when(clasessRepository.findById(id)).thenReturn(Optional.of(entity));
        when(clasessMapper.toResponse(entity)).thenReturn(response);

        ApiResult<ClasessResponse> result = clasessService.findById(id);

        assertThat(result.data()).isEqualTo(response);
    }

    @Test
    void findById_rejectsMissingClass() {
        UUID id = UUID.randomUUID();
        when(clasessRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clasessService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_changesNameAndKeepsCode() {
        UUID id = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        Categoria categoria = new Categoria(categoriaId, "Audio", "10", null, null);
        Clasess entity = new Clasess(id, "1010", "Audífonos", categoria, null, null);
        ClasessResponse response = new ClasessResponse(id, "1010", "Audífonos inalámbricos", "Audio", null, null);
        ClasessUpdateRequest request = new ClasessUpdateRequest("Audífonos inalámbricos");

        when(clasessRepository.findById(id)).thenReturn(Optional.of(entity));
        when(clasessRepository.existsByNombreIgnoreCaseAndCategoriaIdAndIdNot(
                request.nombre(), categoriaId, id
        )).thenReturn(false);
        when(clasessRepository.saveAndFlush(entity)).thenReturn(entity);
        when(clasessMapper.toResponse(entity)).thenReturn(response);

        ApiResult<ClasessResponse> result = clasessService.update(id, request);

        assertThat(entity.getCodigo()).isEqualTo("1010");
        assertThat(result.data()).isEqualTo(response);
        verify(clasessRepository).saveAndFlush(entity);
    }

    @Test
    void update_rejectsDuplicateName() {
        UUID id = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        Categoria categoria = new Categoria(categoriaId, "Audio", "10", null, null);
        Clasess entity = new Clasess(id, "1010", "Audífonos", categoria, null, null);
        ClasessUpdateRequest request = new ClasessUpdateRequest("Parlantes");

        when(clasessRepository.findById(id)).thenReturn(Optional.of(entity));
        when(clasessRepository.existsByNombreIgnoreCaseAndCategoriaIdAndIdNot(
                request.nombre(), categoriaId, id
        )).thenReturn(true);

        assertThatThrownBy(() -> clasessService.update(id, request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(clasessRepository, never()).saveAndFlush(any(Clasess.class));
    }

    @Test
    void create_rejectsMissingCategory() {
        UUID categoriaId = UUID.randomUUID();
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clasessService.create(
                new ClasessCreateRequest("Audífonos", categoriaId)
        )).isInstanceOf(ResourceNotFoundException.class);

        verify(clasessRepository, never()).saveAndFlush(any(Clasess.class));
    }

    @Test
    void create_rejectsDuplicateName() {
        UUID categoriaId = UUID.randomUUID();
        Categoria categoria = new Categoria(categoriaId, "Audio", "10", null, null);
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(clasessRepository.existsByNombreIgnoreCaseAndCategoriaId("Audífonos", categoriaId))
                .thenReturn(true);

        assertThatThrownBy(() -> clasessService.create(
                new ClasessCreateRequest("Audífonos", categoriaId)
        )).isInstanceOf(DuplicateResourceException.class);

        verify(clasessRepository, never()).saveAndFlush(any(Clasess.class));
    }
}
