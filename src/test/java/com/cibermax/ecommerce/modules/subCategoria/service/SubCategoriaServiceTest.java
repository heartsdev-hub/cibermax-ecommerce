package com.cibermax.ecommerce.modules.subCategoria.service;

import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.categoria.repository.CategoriaRepository;
import com.cibermax.ecommerce.modules.subCategoria.dto.request.SubCategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.subCategoria.dto.request.SubCategoryUpdateRequestDT;
import com.cibermax.ecommerce.modules.subCategoria.dto.response.SubCategoryResponse;
import com.cibermax.ecommerce.modules.subCategoria.entity.SubCategoria;
import com.cibermax.ecommerce.modules.subCategoria.mapper.SubCategoriaMapper;
import com.cibermax.ecommerce.modules.subCategoria.repository.SubCategoriaRepository;
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
class SubCategoriaServiceTest {

    @Mock
    private SubCategoriaRepository subCategoriaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private SubCategoriaMapper subCategoriaMapper;

    @InjectMocks
    private SubCategoriaService subCategoriaService;

    @Test
    @DisplayName("create registra una subcategoría asociada a una categoría existente")
    void create_savesSubCategoryWithParentCategory() {
        UUID categoriaId = UUID.randomUUID();
        UUID subCategoriaId = UUID.randomUUID();
        Instant timestamp = Instant.parse("2026-01-01T10:00:00Z");
        SubCategoryCreateRequestDTO request = new SubCategoryCreateRequestDTO("Laptops", categoriaId);
        Categoria categoria = new Categoria(categoriaId, "Computadoras", null, null);
        SubCategoria entity = new SubCategoria(null, "Laptops", categoria, null, null);
        SubCategoria savedEntity = new SubCategoria(subCategoriaId, "Laptops", categoria, timestamp, timestamp);
        SubCategoryResponse response = new SubCategoryResponse(
                subCategoriaId, "Laptops", categoriaId, "Computadoras", timestamp, timestamp
        );

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(subCategoriaRepository.existsByNombreIgnoreCaseAndCategoriaId("Laptops", categoriaId))
                .thenReturn(false);
        when(subCategoriaMapper.toEntity(request, categoria)).thenReturn(entity);
        when(subCategoriaRepository.saveAndFlush(entity)).thenReturn(savedEntity);
        when(subCategoriaMapper.toResponse(savedEntity)).thenReturn(response);

        ApiResult<SubCategoryResponse> result = subCategoriaService.create(request);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).isEqualTo("Subcategoría creada correctamente");
        assertThat(result.data()).isEqualTo(response);
        verify(subCategoriaRepository).saveAndFlush(entity);
        verify(subCategoriaMapper).toEntity(request, categoria);
    }

    @Test
    @DisplayName("create lanza excepción cuando la categoría padre no existe")
    void create_throwsWhenParentCategoryDoesNotExist() {
        UUID categoriaId = UUID.randomUUID();
        SubCategoryCreateRequestDTO request = new SubCategoryCreateRequestDTO("Laptops", categoriaId);
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subCategoriaService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se encontró la categoría con id: " + categoriaId);

        verify(subCategoriaRepository, never()).saveAndFlush(any(SubCategoria.class));
        verify(subCategoriaMapper, never()).toEntity(any(), any(Categoria.class));
    }

    @Test
    @DisplayName("create rechaza una subcategoría duplicada dentro de la categoría")
    void create_rejectsDuplicateNameInParentCategory() {
        UUID categoriaId = UUID.randomUUID();
        SubCategoryCreateRequestDTO request = new SubCategoryCreateRequestDTO("Laptops", categoriaId);
        Categoria categoria = new Categoria(categoriaId, "Computadoras", null, null);
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(subCategoriaRepository.existsByNombreIgnoreCaseAndCategoriaId("Laptops", categoriaId))
                .thenReturn(true);

        assertThatThrownBy(() -> subCategoriaService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Ya existe una subcategoría con el nombre indicado en la categoría seleccionada");

        verify(subCategoriaRepository, never()).saveAndFlush(any(SubCategoria.class));
        verify(subCategoriaMapper, never()).toEntity(any(), any(Categoria.class));
    }

    @Test
    @DisplayName("findAll retorna las subcategorías con su categoría padre")
    void findAll_returnsSubCategories() {
        UUID categoriaId = UUID.randomUUID();
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        Instant timestamp = Instant.parse("2026-01-01T10:00:00Z");
        Categoria categoria = new Categoria(categoriaId, "Computadoras", null, null);
        SubCategoria first = new SubCategoria(firstId, "Laptops", categoria, timestamp, timestamp);
        SubCategoria second = new SubCategoria(secondId, "Desktops", categoria, timestamp, timestamp);
        SubCategoryResponse firstResponse = new SubCategoryResponse(
                firstId, "Laptops", categoriaId, "Computadoras", timestamp, timestamp
        );
        SubCategoryResponse secondResponse = new SubCategoryResponse(
                secondId, "Desktops", categoriaId, "Computadoras", timestamp, timestamp
        );

        when(subCategoriaRepository.findAll()).thenReturn(List.of(first, second));
        when(subCategoriaMapper.toResponse(first)).thenReturn(firstResponse);
        when(subCategoriaMapper.toResponse(second)).thenReturn(secondResponse);

        ApiResult<List<SubCategoryResponse>> result = subCategoriaService.findAll();

        assertThat(result.success()).isTrue();
        assertThat(result.message()).isEqualTo("Subcategorías obtenidas correctamente");
        assertThat(result.data()).containsExactly(firstResponse, secondResponse);
        verify(subCategoriaMapper).toResponse(first);
        verify(subCategoriaMapper).toResponse(second);
    }

    @Test
    @DisplayName("findAll retorna una lista vacía cuando no existen subcategorías")
    void findAll_returnsEmptyList() {
        when(subCategoriaRepository.findAll()).thenReturn(List.of());

        ApiResult<List<SubCategoryResponse>> result = subCategoriaService.findAll();

        assertThat(result.data()).isEmpty();
        verify(subCategoriaMapper, never()).toResponse(any(SubCategoria.class));
    }

    @Test
    @DisplayName("findById retorna el detalle de una subcategoría existente")
    void findById_returnsSubCategoryDetails() {
        UUID categoriaId = UUID.randomUUID();
        UUID subCategoriaId = UUID.randomUUID();
        Instant timestamp = Instant.parse("2026-01-01T10:00:00Z");
        Categoria categoria = new Categoria(categoriaId, "Computadoras", null, null);
        SubCategoria subCategoria = new SubCategoria(
                subCategoriaId, "Laptops", categoria, timestamp, timestamp
        );
        SubCategoryResponse response = new SubCategoryResponse(
                subCategoriaId, "Laptops", categoriaId, "Computadoras", timestamp, timestamp
        );
        when(subCategoriaRepository.findById(subCategoriaId)).thenReturn(Optional.of(subCategoria));
        when(subCategoriaMapper.toResponse(subCategoria)).thenReturn(response);

        ApiResult<SubCategoryResponse> result = subCategoriaService.findById(subCategoriaId);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).isEqualTo("Subcategoría obtenida correctamente");
        assertThat(result.data()).isEqualTo(response);
        verify(subCategoriaMapper).toResponse(subCategoria);
    }

    @Test
    @DisplayName("findById lanza excepción cuando la subcategoría no existe")
    void findById_throwsWhenSubCategoryDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(subCategoriaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subCategoriaService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se encontró la subcategoría con id: " + id);

        verify(subCategoriaMapper, never()).toResponse(any(SubCategoria.class));
    }

    @Test
    @DisplayName("update modifica el nombre y conserva la categoría padre")
    void update_changesNameAndReturnsUpdatedSubCategory() {
        UUID categoriaId = UUID.randomUUID();
        UUID subCategoriaId = UUID.randomUUID();
        Instant timestamp = Instant.parse("2026-01-01T10:00:00Z");
        Categoria categoria = new Categoria(categoriaId, "Computadoras", null, null);
        SubCategoria entity = new SubCategoria(
                subCategoriaId, "Laptops", categoria, timestamp, timestamp
        );
        SubCategoryUpdateRequestDT request = new SubCategoryUpdateRequestDT("Ultrabooks");
        SubCategoryResponse response = new SubCategoryResponse(
                subCategoriaId, "Ultrabooks", categoriaId, "Computadoras", timestamp, timestamp
        );

        when(subCategoriaRepository.findById(subCategoriaId)).thenReturn(Optional.of(entity));
        when(subCategoriaRepository.existsByNombreIgnoreCaseAndCategoriaIdAndIdNot(
                "Ultrabooks", categoriaId, subCategoriaId
        )).thenReturn(false);
        when(subCategoriaRepository.saveAndFlush(entity)).thenReturn(entity);
        when(subCategoriaMapper.toResponse(entity)).thenReturn(response);

        ApiResult<SubCategoryResponse> result = subCategoriaService.update(subCategoriaId, request);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).isEqualTo("Subcategoría actualizada correctamente");
        assertThat(result.data()).isEqualTo(response);
        verify(subCategoriaMapper).updateEntity(request, entity);
        verify(subCategoriaRepository).saveAndFlush(entity);
    }

    @Test
    @DisplayName("update lanza excepción cuando la subcategoría no existe")
    void update_throwsWhenSubCategoryDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(subCategoriaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subCategoriaService.update(
                id, new SubCategoryUpdateRequestDT("Ultrabooks")
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se encontró la subcategoría con id: " + id);

        verify(subCategoriaRepository, never())
                .existsByNombreIgnoreCaseAndCategoriaIdAndIdNot(any(), any(), any());
        verify(subCategoriaRepository, never()).saveAndFlush(any(SubCategoria.class));
    }

    @Test
    @DisplayName("update rechaza un nombre duplicado dentro de la categoría")
    void update_rejectsDuplicateNameInParentCategory() {
        UUID categoriaId = UUID.randomUUID();
        UUID subCategoriaId = UUID.randomUUID();
        Categoria categoria = new Categoria(categoriaId, "Computadoras", null, null);
        SubCategoria entity = new SubCategoria(
                subCategoriaId, "Laptops", categoria, null, null
        );
        when(subCategoriaRepository.findById(subCategoriaId)).thenReturn(Optional.of(entity));
        when(subCategoriaRepository.existsByNombreIgnoreCaseAndCategoriaIdAndIdNot(
                "Ultrabooks", categoriaId, subCategoriaId
        )).thenReturn(true);

        assertThatThrownBy(() -> subCategoriaService.update(
                subCategoriaId, new SubCategoryUpdateRequestDT("Ultrabooks")
        ))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Ya existe otra subcategoría con el nombre indicado en la categoría seleccionada");

        verify(subCategoriaMapper, never()).updateEntity(any(), any(SubCategoria.class));
        verify(subCategoriaRepository, never()).saveAndFlush(any(SubCategoria.class));
    }
}
