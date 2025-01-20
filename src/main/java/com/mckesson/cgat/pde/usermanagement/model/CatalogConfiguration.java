package com.mckesson.cgat.pde.usermanagement.model;

import com.mckesson.cgat.pde.usermanagement.dto.StepDTO;
import com.mckesson.cgat.pde.usermanagement.mapper.StepDTOConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "catalog_configuration", schema = "catalog", indexes = {
        @Index(name = "idx_material_variant_shipto_soldto", columnList = "materialId, variant, shipToAccount, soldToAccount")},
        uniqueConstraints = {
                @UniqueConstraint(name = "Configuration is already present for this combination", columnNames = {"materialId", "variant", "shipToAccount", "soldToAccount"})
        })

public class CatalogConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String materialId;

    @Column(nullable = false)
    private String variant;

    @Column(nullable = false)
    private String shipToAccount;

    @Column(nullable = false)
    private String soldToAccount;

    @Convert(converter = StepDTOConverter.class)
    @Column(nullable = false, columnDefinition = "text")
    private List<StepDTO> steps;
}
