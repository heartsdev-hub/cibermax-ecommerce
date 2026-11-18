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

    default String formatCodigo(Subclase entity) {
        if (entity.getCodigo() == null
                || entity.getClasess() == null
                || entity.getClasess().getCodigo() == null) {
            return entity.getCodigo();
        }

        String classCode = String.valueOf(entity.getClasess().getCodigo());
        int sequence = extractSequence(entity.getCodigo(), classCode);
        return classCode + sequence;
    }

    default int extractSequence(String code, String classCode) {
        String sequence = code.startsWith(classCode)
                ? code.substring(classCode.length())
                : code.substring(Math.max(0, code.length() - 2));

        try {
            return Integer.parseInt(sequence);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}
