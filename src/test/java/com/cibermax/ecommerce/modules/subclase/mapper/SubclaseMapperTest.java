package com.cibermax.ecommerce.modules.subclase.mapper;

import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.clasess.entity.Clasess;
import com.cibermax.ecommerce.modules.subclase.dto.request.SubclaseCreateRequest;
import com.cibermax.ecommerce.modules.subclase.dto.response.SubclaseResponse;
import com.cibermax.ecommerce.modules.subclase.entity.Subclase;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SubclaseMapperTest {

    private final SubclaseMapper mapper = Mappers.getMapper(SubclaseMapper.class);

    @Test
    void mapsRequestAndClassToEntity() {
        UUID clasessId = UUID.randomUUID();
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", "10", null, null);
        Clasess clasess = new Clasess(clasessId, "1010", "Audífonos", categoria, null, null);
        SubclaseCreateRequest request = new SubclaseCreateRequest("Inalámbricos", clasessId);

        Subclase entity = mapper.toEntity(request, clasess);

        assertThat(entity.getNombre()).isEqualTo("Inalámbricos");
        assertThat(entity.getCodigo()).isNull();
        assertThat(entity.getClasess()).isSameAs(clasess);
    }

    @Test
    void mapsEntityWithCategoryAndClassDataToResponse() {
        Categoria categoria = new Categoria(UUID.randomUUID(), "Audio", "10", null, null);
        Clasess clasess = new Clasess(UUID.randomUUID(), "1010", "Audífonos", categoria, null, null);
        Subclase entity = new Subclase(UUID.randomUUID(), "101020", "Inalámbricos", clasess, null, null);

        SubclaseResponse response = mapper.toResponse(entity);

        assertThat(response.codigo()).isEqualTo("101020");
        assertThat(response.nombre()).isEqualTo("Inalámbricos");
        assertThat(response.categoria()).isEqualTo("Audio");
        assertThat(response.clase()).isEqualTo("Audífonos");
    }
}
