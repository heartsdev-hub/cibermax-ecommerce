package com.cibermax.ecommerce.modules.categoria.mapper;

import com.cibermax.ecommerce.modules.categoria.dto.request.CategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.categoria.dto.request.CategoryUpdateRequestDT;
import com.cibermax.ecommerce.modules.categoria.dto.response.CategoryResponse;
import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Categoria toEntity(CategoryCreateRequestDTO request);

    CategoryResponse toResponse(Categoria entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CategoryUpdateRequestDT request, @MappingTarget Categoria entity);
}
