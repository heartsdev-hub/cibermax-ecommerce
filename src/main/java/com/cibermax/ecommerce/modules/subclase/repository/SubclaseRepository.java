package com.cibermax.ecommerce.modules.subclase.repository;

import com.cibermax.ecommerce.modules.subclase.entity.Subclase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubclaseRepository extends JpaRepository<Subclase, UUID> {

    List<Subclase> findAllByOrderByCodigoAsc();

    List<Subclase> findByClasessIdOrderByCodigoAsc(UUID clasessId);

    boolean existsByNombreIgnoreCaseAndClasessId(String nombre, UUID clasessId);
}
