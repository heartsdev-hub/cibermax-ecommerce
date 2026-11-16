package com.cibermax.ecommerce.modules.categoria.mapper;

import com.cibermax.ecommerce.modules.categoria.dto.request.CategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaMapperTest {

    private final CategoriaMapper categoriaMapper = Mappers.getMapper(CategoriaMapper.class);

    @Test
    @DisplayName("convierte CategoryCreateRequestDTO a Categoria")
    void toEntity_mapsCreateRequest() {
        CategoryCreateRequestDTO request = new CategoryCreateRequestDTO("Tecnología");

        Categoria entity = categoriaMapper.toEntity(request);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getNombre()).isEqualTo("Tecnología");
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }
}
