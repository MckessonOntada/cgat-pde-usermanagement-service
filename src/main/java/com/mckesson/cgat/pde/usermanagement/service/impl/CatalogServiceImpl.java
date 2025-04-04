package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogDetailsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CatalogConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.dto.CatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.repository.CatalogServiceRepository;
import com.mckesson.cgat.pde.usermanagement.service.CatalogService;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
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

	private final QuestionnaireService questionnaireService;

	public CatalogServiceImpl(CatalogServiceRepository catalogServiceRepository,
			QuestionnaireService questionnaireService) {
		this.catalogServiceRepository = catalogServiceRepository;
		this.questionnaireService = questionnaireService;
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
	public CatalogDetailsResponseDTO findCatalogConfiguration(String materialId, String variant, String shipToAccount,
			String soldToAccount, ChannelType channelType) throws ResourceNotFoundException {
		logSearchDetails(materialId, variant, shipToAccount, soldToAccount);
		CatalogConfigId id = buildCatalogConfigId(materialId, variant, shipToAccount, soldToAccount);
		CatalogResponseDTO catalogResponseDTO = catalogServiceRepository.findCatalogDetailsById(id)
			.orElseThrow(() -> new ResourceNotFoundException(CATALOG_CONFIGURATION_NOT_FOUND));

		List<Questionnaire> questionnaires = questionnaireService.getAllConfiguredQuestionnairesForSpecificType(
				QuestionnaireConfigurationType.Catalog, materialId, soldToAccount, shipToAccount, variant, channelType);

		CatalogDetailsResponseDTO catalogDetailsResponseDTO = CatalogDetailsResponseDTO.builder()
			.questionnaires(questionnaires)
			.build();

		catalogDetailsResponseDTO.setCatalogConfiguration(catalogResponseDTO.getCatalogConfiguration());
		catalogDetailsResponseDTO.setCatalogPrice(catalogResponseDTO.getCatalogPrice());
		return catalogDetailsResponseDTO;
	}

	@Override
	@Transactional
	public void deleteCatalogConfiguration(String materialId, String variant, String shipToAccount,
			String soldToAccount) throws ResourceNotFoundException {
		CatalogConfigId id = buildCatalogConfigId(materialId, variant, shipToAccount, soldToAccount);

		int deletedCount = catalogServiceRepository.removeCatalogConfigurationById(id);

		if (deletedCount == 0) {
			throw new ResourceNotFoundException(CATALOG_CONFIGURATION_NOT_FOUND);
		}

		log.info("Configuration deleted for materialId: {}, variant: {}, shipToAccount: {}, soldToAccount: {}",
				materialId, variant, shipToAccount, soldToAccount);
	}

	@Override
	public CatalogConfiguration findCatalogConf(String materialId, String variant, String shipToAccount,
			String soldToAccount) {
		logSearchDetails(materialId, variant, shipToAccount, soldToAccount);
		CatalogConfigId id = buildCatalogConfigId(materialId, variant, shipToAccount, soldToAccount);
		return catalogServiceRepository.findById(id).orElseGet(() -> {
			log.info(
					"Catalog configuration not found for materialId: {}, variant: {}, shipToAccount: {}, soldToAccount: {}",
					materialId, variant, shipToAccount, soldToAccount);
			return new CatalogConfiguration();
		});
	}

	private void logSearchDetails(String materialId, String variant, String shipToAccount, String soldToAccount) {
		log.info(
				"Searching for CatalogConfiguration with materialId: {}, variant: {}, shipToAccount: {}, soldToAccount: {}",
				materialId, variant, shipToAccount, soldToAccount);
	}

	private CatalogConfigId buildCatalogConfigId(String materialId, String variant, String shipToAccount,
			String soldToAccount) {
		return CatalogConfigId.builder()
			.materialId(materialId)
			.soldToAccount(soldToAccount)
			.shipToAccount(shipToAccount)
			.variant(StringUtils.isNotEmpty(variant) ? variant : UNKNOWN)
			.build();
	}

}
