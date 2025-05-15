package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogConfigurationAttributes;
import com.mckesson.cgat.pde.usermanagement.dto.DeLinkQuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireEntityConfigDTO;
import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.model.*;
import com.mckesson.cgat.pde.usermanagement.repository.MaterialCrossWalkConfigRepository;
import com.mckesson.cgat.pde.usermanagement.repository.QuestionnaireConfigurationRepository;
import com.mckesson.cgat.pde.usermanagement.repository.QuestionnaireRepository;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.jbat.auth.module.exception.BadRequestException;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.getConsolidatedStatus;

@Service
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

	private final QuestionnaireRepository questionnaireRepository;

	private final QuestionnaireConfigurationRepository questionnaireConfigurationRepository;

	private final MaterialCrossWalkConfigRepository materialCrossWalkConfigRepository;

	public QuestionnaireServiceImpl(QuestionnaireRepository questionnaireRepository,
			QuestionnaireConfigurationRepository questionnaireConfigurationRepository,
			MaterialCrossWalkConfigRepository materialCrossWalkConfigRepository) {
		this.questionnaireRepository = questionnaireRepository;
		this.questionnaireConfigurationRepository = questionnaireConfigurationRepository;
		this.materialCrossWalkConfigRepository = materialCrossWalkConfigRepository;
	}

	@Override
	public Questionnaire createQuestionnaire(Questionnaire questionnaire) {
		log.info("saving questionnaire");
		return questionnaireRepository.save(questionnaire);
	}

	@Override
	public Questionnaire updateQuestionnaire(Long questionnaireId, Questionnaire questionnaireObj)
			throws ResourceNotFoundException {
		log.info("updating questionnaire for id: {} ", questionnaireId);

		Questionnaire qs = questionnaireRepository.findById(questionnaireId)
			.orElseThrow(() -> new ResourceNotFoundException("Questionnaire not found for id: {} " + questionnaireId));

		qs.setTemplateName(questionnaireObj.getTemplateName());
		qs.setQuestions(questionnaireObj.getQuestions());
		return questionnaireRepository.save(qs);
	}

	@Override
	@Transactional
	public void configureQuestionnaire(QuestionnaireConfigurationType type,
			QuestionnaireConfigurationDTO questionnaireConfigurationDTO)
			throws ResourceNotFoundException, BadRequestException {
		validateQuestionnaireConfigRequest(type, questionnaireConfigurationDTO);
		List<Questionnaire> questionnaireList = questionnaireRepository
			.findByIdInAndIsActiveTrue(questionnaireConfigurationDTO.getQuestionnaireIds());

		if (questionnaireList.size() != questionnaireConfigurationDTO.getQuestionnaireIds().size()) {
			throw new ResourceNotFoundException("Some Questionnaire Ids not found");
		}

		questionnaireList.forEach(q -> q.getConfiguredAt().add(type));
		Map<ChannelType, Map<String, Set<QuestionnaireConfiguration>>> existingMap = new HashMap<>();

		List<QuestionnaireConfiguration> bnbConfigs = updpateQuestionnaireList(type, ChannelType.BUY_AND_BILL,
				questionnaireConfigurationDTO.getBnbConfigs(), existingMap, questionnaireList);
		existingMap = new HashMap<>();
		List<QuestionnaireConfiguration> spConfigs = updpateQuestionnaireList(type, ChannelType.SPECIALITY_PHARMA,
				questionnaireConfigurationDTO.getSpConfigs(), existingMap, questionnaireList);

		// Combine all configurations into one list
		List<QuestionnaireConfiguration> allConfigs = new ArrayList<>();
		allConfigs.addAll(bnbConfigs != null ? bnbConfigs : Collections.emptyList());
		allConfigs.addAll(spConfigs != null ? spConfigs : Collections.emptyList());
		questionnaireRepository.saveAll(questionnaireList);
		if (!allConfigs.isEmpty()) {
			questionnaireConfigurationRepository.saveAll(allConfigs);
		}

	}

	private void validateQuestionnaireConfigRequest(QuestionnaireConfigurationType type,
			QuestionnaireConfigurationDTO configDTO) throws BadRequestException {
		boolean isValid = switch (type) {
			case Account -> hasAny(configDTO.getBnbConfigs().getAccountNumberList(),
					configDTO.getSpConfigs().getAccountNumberList());
			case Material -> hasAny(configDTO.getBnbConfigs().getMaterialNumberList(),
					configDTO.getSpConfigs().getMaterialNumberList());
			case Catalog -> hasAny(configDTO.getBnbConfigs().getCatalogIdAttributes(),
					configDTO.getSpConfigs().getCatalogIdAttributes());
		};

		if (!isValid) {
			throw new BadRequestException("Either SP or Bnb " + type + " configuration is required: ");
		}
	}

	private <T> boolean hasAny(List<T> bnbList, List<T> spList) {
		return (bnbList != null && !bnbList.isEmpty()) || (spList != null && !spList.isEmpty());
	}

	private List<QuestionnaireConfiguration> updpateQuestionnaireList(QuestionnaireConfigurationType configType,
			ChannelType channelType, QuestionnaireEntityConfigDTO items,
			Map<ChannelType, Map<String, Set<QuestionnaireConfiguration>>> existingMap,
			List<Questionnaire> questionnaireList) throws BadRequestException {
		List<String> configEntityIds = generateEntityIds(configType, items, channelType);
		if (configEntityIds == null || configEntityIds.isEmpty())
			return null;
		List<QuestionnaireConfiguration> existingConfigsOfBnb = questionnaireConfigurationRepository
			.findByEntityIdInAndConfiguredTypeAndChannelType(configEntityIds, configType, channelType);

		existingConfigsOfBnb.forEach(config -> existingMap.computeIfAbsent(channelType, k -> new HashMap<>())
			.computeIfAbsent(config.getEntityId(), k -> new HashSet<>())
			.add(config));

		return questionnaireList.stream().flatMap(questionnaire -> configEntityIds.stream().map(entityId -> {

			Map<String, Set<QuestionnaireConfiguration>> entityMap = existingMap.getOrDefault(channelType,
					Collections.emptyMap());
			Set<QuestionnaireConfiguration> configs = entityMap.getOrDefault(entityId, Collections.emptySet());

			return configs.stream()
				.filter(config -> config.getQuestionnaireId().getId().equals(questionnaire.getId()))
				.findFirst()
				.orElseGet(() -> QuestionnaireConfiguration.builder()
					.configuredType(configType)
					.questionnaireId(questionnaire)
					.entityId(entityId)
					.channelType(channelType)
					.build());
		})).collect(Collectors.toList());
	}

	private List<String> generateEntityIds(QuestionnaireConfigurationType type, QuestionnaireEntityConfigDTO dto,
			ChannelType channelType) throws BadRequestException {
		switch (type) {
			case Account:
				return CollectionUtils.isEmpty(dto.getAccountNumberList()) ? null : dto.getAccountNumberList();

			case Material:
				return CollectionUtils.isEmpty(dto.getMaterialNumberList()) ? null : dto.getMaterialNumberList();

			case Catalog:
				List<CatalogConfigurationAttributes> catalogs = dto.getCatalogIdAttributes();
				if (catalogs == null || catalogs.isEmpty()) {
					return null;
				}
				return catalogs.stream()
					.map(c -> getCatalogId(c.getSapMaterial(), c.getSoldToCustomer(), c.getShipToCustomer(),
							c.getVariant(), channelType.name()))
					.collect(Collectors.toList());

			default:
				throw new BadRequestException("Unknown configuration type: " + type);
		}
	}

	@Override
	@Transactional
	public void deLinkConfiguredQuestionnaire(DeLinkQuestionnaireConfigurationDTO dto,
			QuestionnaireConfigurationType type) throws ResourceNotFoundException {
		Questionnaire questionnaire = questionnaireRepository.findById(dto.getQuestionnaireId())
			.orElseThrow(() -> new ResourceNotFoundException(
					"Questionnaire not found with Id: " + dto.getQuestionnaireId()));

		String entityId = getEntityId(dto.getMaterialNumber(), dto.getAccountNumber(), dto.getCatalogId(), type,
				dto.getChannelType());
		questionnaireConfigurationRepository.deleteByQuestionnaireIdAndEntityIdAndConfiguredTypeAndChannelType(
				dto.getQuestionnaireId(), entityId, type, dto.getChannelType());

		long count = questionnaireConfigurationRepository
			.countByQuestionnaireIdAndConfiguredType(dto.getQuestionnaireId(), type);
		if (count == 0) {
			questionnaire.getConfiguredAt().remove(type);
			questionnaireRepository.save(questionnaire);
		}
	}

	@Override
	public List<Questionnaire> getAllConfiguredQuestionnaires(String sapMaterial, String soldToCustomer,
			String shipToCustomer, String variant, ChannelType channelType) {
		Crosswalk crosswalkDetails = null;
		if (channelType.equals(ChannelType.SPECIALITY_PHARMA)) {
			crosswalkDetails = materialCrossWalkConfigRepository.findBySpProductIdAndIsActive(sapMaterial, true);
		}

		String catalogId = getCatalogId(sapMaterial, soldToCustomer, shipToCustomer, variant, channelType.name());
		List<QuestionnaireConfiguration> allConfiguredQuestionnaires = questionnaireConfigurationRepository
			.getAllConfiguredQuestionnaires(
					ObjectUtils.isEmpty(crosswalkDetails) ? sapMaterial : crosswalkDetails.getBnbMaterialNumber(),
					soldToCustomer, catalogId, channelType.name(), !ObjectUtils.isEmpty(crosswalkDetails));
		return allConfiguredQuestionnaires.stream()
			.map(QuestionnaireConfiguration::getQuestionnaireId)
			.distinct()
			.toList();

	}

	@Override
	public Page<Questionnaire> findAll(Pageable paging, String query, List<String> status) {

		return questionnaireRepository.findAllByQueryText(query, getConsolidatedStatus(status), paging);
	}

	@Override
	public void deactivateQuestionnaire(Long questionnaireId) throws ResourceNotFoundException {
		Questionnaire questionnaire = fetchQuestionnaireById(questionnaireId);
		questionnaire.setActive(false);
		questionnaireRepository.save(questionnaire);
	}

	@Override
	public Questionnaire fetchQuestionnaireById(Long questionnaireId) throws ResourceNotFoundException {
		return questionnaireRepository.findById(questionnaireId)
			.orElseThrow(() -> new ResourceNotFoundException("Questionnaire not found with Id: " + questionnaireId));
	}

	@Override
	public List<Questionnaire> getAllConfiguredQuestionnairesForSpecificType(QuestionnaireConfigurationType type,
			String sapMaterial, String soldToCustomer, String shipToCustomer, String variant, ChannelType channelType) {

		CatalogConfigurationAttributes catalogConfigurationAttributes = CatalogConfigurationAttributes.builder()
			.variant(variant)
			.soldToCustomer(soldToCustomer)
			.shipToCustomer(shipToCustomer)
			.sapMaterial(sapMaterial)
			.build();
		String entityId = getEntityId(sapMaterial, soldToCustomer, catalogConfigurationAttributes, type, channelType);

		List<QuestionnaireConfiguration> allConfiguredQuestionnaires = questionnaireConfigurationRepository
			.getAllConfiguredQuestionnairesForSpecificType(entityId, type.name(), channelType.name());
		List<Questionnaire> questionnaires = allConfiguredQuestionnaires.stream()
			.map(QuestionnaireConfiguration::getQuestionnaireId)
			.distinct()
			.collect(Collectors.toList());
		return questionnaires;

	}

	private String getEntityId(String materialNumber, String accountNumber,
			CatalogConfigurationAttributes catalogAttributes, QuestionnaireConfigurationType type,
			ChannelType channelType) {
		switch (type) {
			case Account:
				if (accountNumber == null)
					throw new IllegalArgumentException("Account Number should not be null");
				return accountNumber;
			case Material:
				if (materialNumber == null)
					throw new IllegalArgumentException("Material Number should not be null");
				return materialNumber;
			case Catalog:
				if (catalogAttributes == null)
					throw new IllegalArgumentException("CatalogId should not be null");
				return getCatalogId(catalogAttributes.getSapMaterial(), catalogAttributes.getSoldToCustomer(),
						catalogAttributes.getShipToCustomer(), catalogAttributes.getVariant(), channelType.name());
			default:
				throw new IllegalArgumentException("Unknown configuration type: " + type);
		}
	}

	private String getCatalogId(String sapMaterial, String soldToCustomer, String shipToCustomer, String variant,
			String channelType) {
		List<String> parts = new ArrayList<>();
		parts.add(sapMaterial);
		parts.add(soldToCustomer);
		parts.add(shipToCustomer);

		if (StringUtils.isNotEmpty(variant)) {
			parts.add(variant);
		}
		parts.add(channelType);

		// Join the parts with "_"
		return String.join("_", parts);
	}

}
