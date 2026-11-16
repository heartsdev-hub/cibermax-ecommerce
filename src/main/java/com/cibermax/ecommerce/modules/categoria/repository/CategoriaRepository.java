package com.cibermax.ecommerce.modules.categoria.repository;

import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, UUID id);

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}
