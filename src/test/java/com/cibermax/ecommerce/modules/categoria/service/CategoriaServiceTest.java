package com.cibermax.ecommerce.modules.categoria.service;

import com.cibermax.ecommerce.modules.categoria.dto.request.CategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.categoria.dto.response.CategoryResponse;
import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.categoria.mapper.CategoriaMapper;
import com.cibermax.ecommerce.modules.categoria.repository.CategoriaRepository;
import com.cibermax.ecommerce.shared.exception.DuplicateResourceException;
import com.cibermax.ecommerce.shared.exception.ResourceNotFoundException;
import com.cibermax.ecommerce.shared.response.ApiResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    @DisplayName("create registra correctamente una categoría")
    void create_savesCategory() {
        CategoryCreateRequestDTO request = new CategoryCreateRequestDTO("Tecnología");
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-01T10:00:00Z");
        Categoria entity = new Categoria(null, "Tecnología", null, null);
        Categoria savedEntity = new Categoria(id, "Tecnología", createdAt, updatedAt);
        CategoryResponse response = new CategoryResponse(id, "Tecnología", createdAt, updatedAt);

        when(categoriaRepository.existsByNombreIgnoreCase(request.nombre())).thenReturn(false);
        when(categoriaMapper.toEntity(request)).thenReturn(entity);
        when(categoriaRepository.save(entity)).thenReturn(savedEntity);
        when(categoriaMapper.toResponse(savedEntity)).thenReturn(response);

        ApiResult<CategoryResponse> result = categoriaService.create(request);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).isEqualTo("Categoría creada correctamente");
        assertThat(result.data()).isEqualTo(response);
        verify(categoriaRepository).save(entity);
        verify(categoriaMapper).toEntity(request);
        verify(categoriaMapper).toResponse(savedEntity);
    }

    @Test
    @DisplayName("create rechaza una categoría con nombre duplicado")
    void create_rejectsDuplicateName() {
        CategoryCreateRequestDTO request = new CategoryCreateRequestDTO("Tecnología");
        when(categoriaRepository.existsByNombreIgnoreCase(request.nombre())).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Ya existe una categoría con el nombre indicado");

        verify(categoriaRepository, never()).save(any(Categoria.class));
        verify(categoriaMapper, never()).toEntity(any(CategoryCreateRequestDTO.class));
    }

    @Test
    @DisplayName("findById retorna el detalle de una categoría existente")
    void findById_returnsCategoryDetails() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Instant timestamp = Instant.parse("2026-01-01T10:00:00Z");
        Categoria categoria = new Categoria(id, "Tecnología", timestamp, timestamp);
        CategoryResponse response = new CategoryResponse(id, "Tecnología", timestamp, timestamp);
        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(categoriaMapper.toResponse(categoria)).thenReturn(response);

        ApiResult<CategoryResponse> result = categoriaService.findById(id);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).isEqualTo("Categoría obtenida correctamente");
        assertThat(result.data()).isEqualTo(response);
        verify(categoriaMapper).toResponse(categoria);
    }

    @Test
    @DisplayName("findById lanza excepción cuando la categoría no existe")
    void findById_throwsWhenCategoryDoesNotExist() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(categoriaMapper, never()).toResponse(any(Categoria.class));
    }
}
