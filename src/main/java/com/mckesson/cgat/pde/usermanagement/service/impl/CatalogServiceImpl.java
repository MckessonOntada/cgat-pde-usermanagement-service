package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.model.CatalogConfiguration;
import com.mckesson.cgat.pde.usermanagement.repository.CatalogServiceRepository;
import com.mckesson.cgat.pde.usermanagement.service.CatalogService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CatalogServiceImpl implements CatalogService {

	private static final String CATALOG_CONFIGURATION_NOT_FOUND = "Catalog configuration not found with the provided catalog attributes";

	private final CatalogServiceRepository catalogServiceRepository;

	public CatalogServiceImpl(CatalogServiceRepository catalogServiceRepository) {
		this.catalogServiceRepository = catalogServiceRepository;
	}

	@Override
	public List<CatalogConfiguration> saveCatalogConfiguration(CatalogConfigurationDTO configurationDTO) {
		log.info("Starting to save catalog configurations for {} catalog prices.",
				configurationDTO.getCatalogConfigurationAttributes().size());

		List<CatalogConfiguration> catalogConfigurations = configurationDTO.getCatalogConfigurationAttributes()
			.stream()
			.map(catalogPrice -> CatalogConfiguration.builder()
				.materialId(catalogPrice.getSapMaterial())
				.variant(catalogPrice.getVariant())
				.shipToAccount(catalogPrice.getShipToCustomer())
				.soldToAccount(catalogPrice.getSoldToCustomer())
				.steps(configurationDTO.getSteps())
				.build())
			.toList();

		log.info("Saving {} catalog configurations to the database.", catalogConfigurations.size());
		return catalogServiceRepository.saveAll(catalogConfigurations);

	}

	@Override
	public CatalogConfiguration findCatalogConfigurationByFields(String materialId, String variant,
			String shipToAccount, String soldToAccount) throws ResourceNotFoundException {
		log.info(
				"Searching for CatalogConfiguration with materialId: {}, variant: {}, shipToAccount: {}, soldToAccount: {}",
				materialId, variant, shipToAccount, soldToAccount);

		return catalogServiceRepository
			.findByMaterialIdAndVariantAndShipToAccountAndSoldToAccount(materialId, variant, shipToAccount,
					soldToAccount)
			.orElseThrow(() -> {
				log.error(
						"CatalogConfiguration not found with materialId: {}, variant: {}, shipToAccount: {}, soldToAccount: {}",
						materialId, variant, shipToAccount, soldToAccount);
				return new ResourceNotFoundException(CATALOG_CONFIGURATION_NOT_FOUND);
			});
	}

}
