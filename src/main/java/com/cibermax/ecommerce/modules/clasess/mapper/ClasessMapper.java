package com.cibermax.ecommerce.modules.clasess.mapper;

import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.clasess.dto.request.ClasessCreateRequest;
import com.cibermax.ecommerce.modules.clasess.dto.request.ClasessUpdateRequest;
import com.cibermax.ecommerce.modules.clasess.dto.response.ClasessResponse;
import com.cibermax.ecommerce.modules.clasess.entity.Clasess;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClasessMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigo", ignore = true)
    @Mapping(target = "nombre", source = "request.nombre")
    @Mapping(target = "categoria", source = "categoria")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Clasess toEntity(ClasessCreateRequest request, Categoria categoria);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigo", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ClasessUpdateRequest request, @MappingTarget Clasess entity);

    @Mapping(target = "categoria", source = "categoria.nombre")
    @Mapping(target = "codigo", expression = "java(formatCodigo(entity))")
    ClasessResponse toResponse(Clasess entity);

    default Integer formatCodigo(Clasess entity) {
        if (entity.getCodigo() == null || entity.getCategoria() == null || entity.getCategoria().getCodigo() == null) {
            return entity.getCodigo();
        }

        return entity.getCategoria().getCodigo() * 100 + Math.abs(entity.getCodigo()) % 100;
    }
}
