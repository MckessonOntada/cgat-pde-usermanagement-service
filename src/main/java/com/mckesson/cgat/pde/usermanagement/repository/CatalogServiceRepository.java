package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.CatalogConfigId;
import com.mckesson.cgat.pde.usermanagement.model.CatalogConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogServiceRepository extends JpaRepository<CatalogConfiguration, CatalogConfigId> {

	@Query("""
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.CatalogResponseDTO(c, p)
			    FROM CatalogConfiguration c
			    LEFT JOIN CatalogPrice p
			    ON c.id.materialId = p.sapMaterial
			    AND c.id.soldToAccount = p.soldToCustomer
			    AND c.id.shipToAccount = p.shipToCustomer
			    AND (c.id.variant = p.variant
			         OR (c.id.variant = 'unknown' AND p.variant IS NULL)
			    )
			    WHERE c.id = :id
			""")
	Optional<CatalogResponseDTO> findCatalogDetailsById(@Param("id") CatalogConfigId id);

}
