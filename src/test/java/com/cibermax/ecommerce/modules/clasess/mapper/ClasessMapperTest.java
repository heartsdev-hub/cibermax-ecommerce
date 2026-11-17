package com.cibermax.ecommerce.modules.clasess.mapper;

import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.clasess.dto.request.ClasessCreateRequest;
import com.cibermax.ecommerce.modules.clasess.dto.response.ClasessResponse;
import com.cibermax.ecommerce.modules.clasess.entity.Clasess;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ClasessMapperTest {

    private final ClasessMapper mapper = Mappers.getMapper(ClasessMapper.class);

    @Test
    void mapsRequestAndCategoryToEntity() {
        UUID categoriaId = UUID.randomUUID();
        Categoria categoria = new Categoria(categoriaId, "Audio", 10, null, null);
        ClasessCreateRequest request = new ClasessCreateRequest("Audífonos", categoriaId);

        Clasess entity = mapper.toEntity(request, categoria);

        assertThat(entity.getNombre()).isEqualTo("Audífonos");
        assertThat(entity.getCodigo()).isNull();
        assertThat(entity.getCategoria()).isSameAs(categoria);
    }

    @Test
    void mapsEntityWithCategoryDataToResponse() {
        UUID categoriaId = UUID.randomUUID();
        Categoria categoria = new Categoria(categoriaId, "Audio", 10, null, null);
        Clasess entity = new Clasess(UUID.randomUUID(), 6050, "Audífonos", categoria, null, null);

        ClasessResponse response = mapper.toResponse(entity);

        assertThat(response.codigo()).isEqualTo(1050);
        assertThat(response.categoria()).isEqualTo("Audio");
    }
}
