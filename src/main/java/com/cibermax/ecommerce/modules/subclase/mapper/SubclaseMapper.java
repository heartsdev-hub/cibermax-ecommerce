package com.cibermax.ecommerce.modules.subclase.mapper;

import com.cibermax.ecommerce.modules.clasess.entity.Clasess;
import com.cibermax.ecommerce.modules.subclase.dto.request.SubclaseCreateRequest;
import com.cibermax.ecommerce.modules.subclase.dto.response.SubclaseResponse;
import com.cibermax.ecommerce.modules.subclase.entity.Subclase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubclaseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigo", ignore = true)
    @Mapping(target = "nombre", source = "request.nombre")
    @Mapping(target = "clasess", source = "clasess")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Subclase toEntity(SubclaseCreateRequest request, Clasess clasess);

    @Mapping(target = "categoria", source = "clasess.categoria.nombre")
    @Mapping(target = "clase", source = "clasess.nombre")
    @Mapping(target = "codigo", expression = "java(formatCodigo(entity))")
    SubclaseResponse toResponse(Subclase entity);

    default Integer formatCodigo(Subclase entity) {
        if (entity.getCodigo() == null
                || entity.getClasess() == null
                || entity.getClasess().getCodigo() == null) {
            return entity.getCodigo();
        }

        return entity.getClasess().getCodigo() * 100 + Math.abs(entity.getCodigo()) % 100;
    }
}
