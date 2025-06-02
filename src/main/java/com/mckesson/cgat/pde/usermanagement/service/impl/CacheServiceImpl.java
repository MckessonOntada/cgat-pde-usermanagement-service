package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.MaterialDetailsResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.MaterialResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.dto.TradeCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.mapper.TradeCatalogResponseMapper;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyConfigRepository;
import com.mckesson.cgat.pde.usermanagement.repository.TherapyRepository;
import com.mckesson.cgat.pde.usermanagement.service.CacheService;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.List;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.ALL;
import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.getConsolidatedChannelType;
import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.getConsolidatedStatus;

@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

	private final TherapyRepository therapyRepository;

	private final TherapyConfigRepository therapyConfigRepository;

	private final QuestionnaireService questionnaireService;

	private final CacheManager cacheManager;

	public CacheServiceImpl(TherapyRepository therapyRepository, TherapyConfigRepository therapyConfigRepository,
			QuestionnaireService questionnaireService /* ,CacheManager cacheManager */) {
		this.therapyRepository = therapyRepository;
		this.therapyConfigRepository = therapyConfigRepository;
		this.questionnaireService = questionnaireService;
		this.cacheManager = null;
	}

	@Cacheable(value = "materialCache", key = "#query + '-' + #paging.pageNumber + '-' + #paging.pageSize")
	public Page<TradeCatalogResponseDTO> findAllGlobalTherapies(String query, Pageable paging) {
		try {
			log.info(" Inside method - findAllGlobalTherapies");
			log.info("query: {}", StringUtils.toRootLowerCase(query));
			Page<TherapyCatalogResponseDTO> projections = therapyRepository.findAllGlobalTherapies(
					StringUtils.toRootLowerCase(query), ALL, ChannelType.BUY_AND_BILL.name(), paging, false);

			Page<TradeCatalogResponseDTO> map = projections.map(TradeCatalogResponseMapper::mapToTradeCatalogResponse);
			return map;
		}
		catch (Exception e) {
			log.error("Exception occurred in findAllGlobalTherapies and exception details", e);
			throw e;
		}
	}

	@Override
	@Cacheable(value = "singleMaterialCache", key = "#materialNumber")
	public List<MaterialDetailsResponseDTO> findTherapyDetailsByTradeName(String tradeName, ChannelType channelType)
			throws ResourceNotFoundException {
		List<MaterialResponseDTO> responseDTOList = therapyConfigRepository.findDetailesByTradeName(tradeName,
				channelType);

		List<Questionnaire> questionnaires = questionnaireService.getAllConfiguredQuestionnairesForSpecificType(
				QuestionnaireConfigurationType.Material, "", "", "", "", ChannelType.BUY_AND_BILL,
				responseDTOList.get(0).getTherapy().getNdcNumber());

		MaterialDetailsResponseDTO materialDetailsResponseDTO = MaterialDetailsResponseDTO.builder()
			.questionnaires(questionnaires)
			.build();
		materialDetailsResponseDTO.setTherapy(responseDTOList.get(0).getTherapy());
		materialDetailsResponseDTO.setMaterialConfig(responseDTOList.get(0).getMaterialConfig());
		return List.of(materialDetailsResponseDTO);
	}

	@Override
	@Transactional
	@CacheEvict(value = "singleMaterialCache", key = "#materialNumber")
	public void deleteConfigByMaterialNumber(String materialNumber) throws ResourceNotFoundException {
		int deletedCount = therapyConfigRepository.removeTherapyConfigByMaterialNumberAndChannelType(materialNumber,
				ChannelType.BUY_AND_BILL);

		if (deletedCount == 0) {
			throw new ResourceNotFoundException(
					String.format("Therapy config not found with materialNumber: %s", materialNumber));
		}
	}

	public void testCacheEvents() {
		Cache<String, Object> cache = cacheManager.getCache("defaultCache", String.class, Object.class);
		if (cache == null) {
			log.error("❌ Cache 'defaultCache' not found!");
			return;
		}
		String testKey = "testKey";

		log.info("📝 Adding an entry to the cache with key: {}", testKey);
		cache.put(testKey, new MaterialDetailsResponseDTO());
		log.info("✔️ Entry added successfully!");

		Object value = cache.get(testKey);
		if (value != null) {
			log.info("✅ Cache hit: Retrieved value for key: {}", testKey);
		}
		else {
			log.warn("⚠️ Cache miss: No value found for key: {}", testKey);
		}
		cache.put(testKey, new MaterialDetailsResponseDTO());
		log.info("✔️ Cache entry updated successfully!");

		cache.remove(testKey);
		log.info("✔️ Cache entry removed successfully!");

		if (cache.get(testKey) == null) {
			log.info("✅ Key '{}' successfully removed from cache!", testKey);
		}
		else {
			log.warn("⚠️ Key '{}' still exists in cache after removal!", testKey);
		}
	}

}
