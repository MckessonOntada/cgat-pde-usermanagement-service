package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.CatalogConfigId;
import com.mckesson.cgat.pde.usermanagement.model.CatalogConfiguration;
import com.mckesson.cgat.pde.usermanagement.repository.CatalogServiceRepository;
import com.mckesson.cgat.pde.usermanagement.service.CatalogService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CatalogServiceImpl implements CatalogService {

	private static final String CATALOG_CONFIGURATION_NOT_FOUND = "Catalog configuration not found with the provided catalog attributes";

	private static final String UNKNOWN = "unknown";

	private final CatalogServiceRepository catalogServiceRepository;

	public CatalogServiceImpl(CatalogServiceRepository catalogServiceRepository) {
		this.catalogServiceRepository = catalogServiceRepository;
	}

	@Override
	public List<CatalogConfiguration> saveCatalogConfiguration(CatalogConfigurationDTO configurationDTO) {
		log.info("Starting to save {} catalog configurations.",
				configurationDTO.getCatalogConfigurationAttributes().size());

		List<CatalogConfiguration> catalogConfigurations = configurationDTO.getCatalogConfigurationAttributes()
			.stream()
			.map(attr -> {
				CatalogConfigId id = CatalogConfigId.builder()
					.soldToAccount(attr.getSoldToCustomer())
					.shipToAccount(attr.getShipToCustomer())
					.materialId(attr.getSapMaterial())
					.variant(StringUtils.isNotEmpty(attr.getVariant()) ? attr.getVariant() : UNKNOWN)
					.build();

				return CatalogConfiguration.builder().id(id).steps(configurationDTO.getSteps()).build();
			})
			.toList();

		log.info("Saving {} catalog configurations to the database.", catalogConfigurations.size());
		return catalogServiceRepository.saveAll(catalogConfigurations);
	}

	@Override
	public CatalogResponseDTO findCatalogConfiguration(String materialId, String variant, String shipToAccount,
			String soldToAccount) throws ResourceNotFoundException {
		log.info(
				"Searching for CatalogConfiguration with materialId: {}, variant: {}, shipToAccount: {}, soldToAccount: {}",
				materialId, variant, shipToAccount, soldToAccount);
		CatalogConfigId id = CatalogConfigId.builder()
			.materialId(materialId)
			.variant(StringUtils.isNotEmpty(variant) ? variant : UNKNOWN)
			.soldToAccount(soldToAccount)
			.shipToAccount(shipToAccount)
			.build();
		return catalogServiceRepository.findCatalogDetailsById(id)
			.orElseThrow(() -> new ResourceNotFoundException(CATALOG_CONFIGURATION_NOT_FOUND));
	}

}
