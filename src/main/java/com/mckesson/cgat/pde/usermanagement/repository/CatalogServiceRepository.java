package com.mckesson.cgat.pde.usermanagement.repository;

import com.mckesson.cgat.pde.usermanagement.model.CatalogConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogServiceRepository extends JpaRepository<CatalogConfiguration, Long> {

	Optional<CatalogConfiguration> findByMaterialIdAndVariantAndShipToAccountAndSoldToAccount(String materialId,
			String variant, String shipToAccount, String soldToAccount);

}
