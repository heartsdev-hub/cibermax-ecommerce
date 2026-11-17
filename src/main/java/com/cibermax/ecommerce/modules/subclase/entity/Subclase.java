package com.cibermax.ecommerce.modules.subclase.entity;

import com.cibermax.ecommerce.modules.clasess.entity.Clasess;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "subclases",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_subclase_codigo", columnNames = "codigo"),
                @UniqueConstraint(
                        name = "uk_subclase_nombre_clasess",
                        columnNames = {"nombre", "clasess_id"}
                )
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Subclase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private Integer codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clasess_id", nullable = false)
    private Clasess clasess;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
