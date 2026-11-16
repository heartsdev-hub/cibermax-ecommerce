package com.cibermax.ecommerce.modules.subCategoria.mapper;

import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import com.cibermax.ecommerce.modules.subCategoria.dto.request.SubCategoryCreateRequestDTO;
import com.cibermax.ecommerce.modules.subCategoria.dto.request.SubCategoryUpdateRequestDT;
import com.cibermax.ecommerce.modules.subCategoria.dto.response.SubCategoryResponse;
import com.cibermax.ecommerce.modules.subCategoria.entity.SubCategoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubCategoriaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombre", source = "request.nombre")
    @Mapping(target = "categoria", source = "categoria")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SubCategoria toEntity(SubCategoryCreateRequestDTO request, Categoria categoria);

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    SubCategoryResponse toResponse(SubCategoria entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(SubCategoryUpdateRequestDT request, @MappingTarget SubCategoria entity);
}
