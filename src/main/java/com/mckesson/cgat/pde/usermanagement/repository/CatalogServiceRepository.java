package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.CatalogConfigId;
import com.mckesson.cgat.pde.usermanagement.model.CatalogConfiguration;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogServiceRepository extends JpaRepository<CatalogConfiguration, CatalogConfigId> {

	@Query("""
			    SELECT new com.mckesson.cgat.pde.usermanagement.dto.CatalogResponseDTO(c, p)
			    FROM CatalogPrice p
			      JOIN Therapy m
			   ON m.materialNumber = p.sapMaterial
			  AND m.channelType = :channelType
			    LEFT JOIN CatalogConfiguration c
			    ON c.id.materialId = p.sapMaterial
			    AND c.id.soldToAccount = p.soldToCustomer
			    AND c.id.shipToAccount = p.shipToCustomer
			    AND c.channelType = :channelType
			    AND (c.id.variant = p.variant
			         OR (c.id.variant = 'unknown' AND p.variant IS NULL)
			    )
			    WHERE p.sapMaterial = :#{#id.materialId}
			    AND p.soldToCustomer = :#{#id.soldToAccount}
			    AND p.shipToCustomer = :#{#id.shipToAccount}
			    AND (p.variant = :#{#id.variant} OR (p.variant IS NULL AND :#{#id.variant} = 'unknown'))
			    AND p.channelType = :channelType
			    AND p.deleted = false
			""")
	Optional<CatalogResponseDTO> findCatalogDetailsById(@Param("id") CatalogConfigId id, ChannelType channelType);

	int removeCatalogConfigurationByIdAndChannelType(CatalogConfigId id, ChannelType channelType);

}
