package com.cibermax.ecommerce.modules.subCategoria.mapper;

import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.subCategoria.dto.request.SubCategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.subCategoria.dto.request.SubCategoryUpdateRequestDT;
import com.cibermax.ecommerce.modules.subCategoria.dto.response.SubCategoryResponse;
import com.cibermax.ecommerce.modules.subCategoria.entity.SubCategoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SubCategoriaMapperTest {

    private final SubCategoriaMapper mapper = Mappers.getMapper(SubCategoriaMapper.class);

    @Test
    @DisplayName("convierte la solicitud de creación y la categoría padre a entidad")
    void toEntity_mapsRequestAndParentCategory() {
        UUID categoriaId = UUID.randomUUID();
        Categoria categoria = new Categoria(categoriaId, "Computadoras", null, null);
        SubCategoryCreateRequestDTO request = new SubCategoryCreateRequestDTO("Laptops", categoriaId);

        SubCategoria entity = mapper.toEntity(request, categoria);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getNombre()).isEqualTo("Laptops");
        assertThat(entity.getCategoria()).isSameAs(categoria);
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("convierte la entidad y expone los datos de la categoría padre")
    void toResponse_mapsParentCategoryData() {
        UUID subCategoriaId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Categoria categoria = new Categoria(categoriaId, "Computadoras", null, null);
        SubCategoria entity = new SubCategoria(
                subCategoriaId,
                "Laptops",
                categoria,
                createdAt,
                createdAt
        );

        SubCategoryResponse response = mapper.toResponse(entity);

        assertThat(response.id()).isEqualTo(subCategoriaId);
        assertThat(response.nombre()).isEqualTo("Laptops");
        assertThat(response.categoriaId()).isEqualTo(categoriaId);
        assertThat(response.categoriaNombre()).isEqualTo("Computadoras");
        assertThat(response.createdAt()).isEqualTo(createdAt);
        assertThat(response.updatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("actualiza únicamente el nombre de la entidad")
    void updateEntity_updatesOnlyName() {
        UUID categoriaId = UUID.randomUUID();
        UUID subCategoriaId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-02T10:00:00Z");
        Categoria categoria = new Categoria(categoriaId, "Computadoras", null, null);
        SubCategoria entity = new SubCategoria(
                subCategoriaId, "Laptops", categoria, createdAt, updatedAt
        );

        mapper.updateEntity(new SubCategoryUpdateRequestDT("Ultrabooks"), entity);

        assertThat(entity.getNombre()).isEqualTo("Ultrabooks");
        assertThat(entity.getId()).isEqualTo(subCategoriaId);
        assertThat(entity.getCategoria()).isSameAs(categoria);
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
    }
}
