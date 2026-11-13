package com.cibermax.ecommerce.modules.categoria.repository;

import com.cibermax.ecommerce.modules.categoria.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    boolean existsByNombreIgnoreCase(String nombre);

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}
