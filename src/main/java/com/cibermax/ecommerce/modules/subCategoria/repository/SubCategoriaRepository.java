package com.cibermax.ecommerce.modules.subCategoria.repository;

import com.cibermax.ecommerce.modules.subCategoria.entity.SubCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubCategoriaRepository extends JpaRepository<SubCategoria, UUID> {

    boolean existsByNombreIgnoreCaseAndCategoriaId(String nombre, UUID categoriaId);
}
