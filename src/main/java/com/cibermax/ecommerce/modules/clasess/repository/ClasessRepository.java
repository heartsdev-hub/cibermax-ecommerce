package com.cibermax.ecommerce.modules.clasess.repository;

import com.cibermax.ecommerce.modules.clasess.entity.Clasess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClasessRepository extends JpaRepository<Clasess, UUID> {

    List<Clasess> findAllByOrderByCodigoAsc();

    List<Clasess> findByCategoriaIdOrderByCodigoAsc(UUID categoriaId);

    boolean existsByNombreIgnoreCaseAndCategoriaId(String nombre, UUID categoriaId);

    boolean existsByNombreIgnoreCaseAndCategoriaIdAndIdNot(String nombre, UUID categoriaId, UUID id);
}
