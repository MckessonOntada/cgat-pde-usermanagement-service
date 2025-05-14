package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogConfigurationAttributes;
import com.mckesson.cgat.pde.usermanagement.dto.DeLinkQuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfiguration;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
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

import java.util.*;
import java.util.stream.Collectors;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.getConsolidatedStatus;

@Service
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

	private final QuestionnaireRepository questionnaireRepository;

	private final QuestionnaireConfigurationRepository questionnaireConfigurationRepository;

	public QuestionnaireServiceImpl(QuestionnaireRepository questionnaireRepository,
			QuestionnaireConfigurationRepository questionnaireConfigurationRepository) {
		this.questionnaireRepository = questionnaireRepository;
		this.questionnaireConfigurationRepository = questionnaireConfigurationRepository;
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
		List<Questionnaire> questionnaireList = questionnaireRepository
			.findByIdInAndIsActiveTrue(questionnaireConfigurationDTO.getQuestionnaireIds());

		if (questionnaireList.size() != questionnaireConfigurationDTO.getQuestionnaireIds().size()) {
			throw new ResourceNotFoundException("Some Questionnaire Ids not found");
		}

		questionnaireList.forEach(q -> q.getConfiguredAt().add(type));

		List<String> entityIds = generateEntityIds(type, questionnaireConfigurationDTO);

		List<QuestionnaireConfiguration> existingConfigs = questionnaireConfigurationRepository
			.findByEntityIdInAndConfiguredType(entityIds, type);

		Map<String, Set<QuestionnaireConfiguration>> existingMap = new HashMap<>();

		existingConfigs
			.forEach(config -> existingMap.computeIfAbsent(config.getEntityId(), k -> new HashSet<>()).add(config));

		List<QuestionnaireConfiguration> questionnaireConfigurationList = questionnaireList.stream()
			.flatMap(questionnaire -> entityIds.stream()
				.map(entityId -> existingMap.getOrDefault(entityId, Collections.emptySet())
					.stream()
					.filter(config -> config.getQuestionnaireId().getId().equals(questionnaire.getId()))
					.findFirst()
					.orElseGet(() -> QuestionnaireConfiguration.builder()
						.configuredType(type)
						.questionnaireId(questionnaire)
						.entityId(entityId)
						.channelType(questionnaireConfigurationDTO.getChannelType())
						.build())))
			.collect(Collectors.toList());

		questionnaireRepository.saveAll(questionnaireList);
		questionnaireConfigurationRepository.saveAll(questionnaireConfigurationList);

	}

	private List<String> generateEntityIds(QuestionnaireConfigurationType type,
			QuestionnaireConfigurationDTO questionnaireConfigurationDTO) throws BadRequestException {
		switch (type) {
			case Account:
				if (questionnaireConfigurationDTO.getAccountNumberList() == null
						|| questionnaireConfigurationDTO.getAccountNumberList().isEmpty()) {
					throw new BadRequestException("Account Number List should not be null or Empty");
				}
				return questionnaireConfigurationDTO.getAccountNumberList();

			case Material:
				if (questionnaireConfigurationDTO.getMaterialNumberList() == null
						|| questionnaireConfigurationDTO.getMaterialNumberList().isEmpty()) {
					throw new BadRequestException("Material Number List should not be null or Empty");
				}
				return questionnaireConfigurationDTO.getMaterialNumberList();

			case Catalog:
				if (questionnaireConfigurationDTO.getCatalogIdAttributes() == null
						|| questionnaireConfigurationDTO.getCatalogIdAttributes().isEmpty()) {
					throw new BadRequestException("Catalog Id List should not be null or Empty");
				}
				return questionnaireConfigurationDTO.getCatalogIdAttributes()
					.stream()
					.map(catalog -> getCatalogId(catalog.getSapMaterial(), catalog.getSoldToCustomer(),
							catalog.getShipToCustomer(), catalog.getVariant()))
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

		String entityId = getEntityId(dto.getMaterialNumber(), dto.getAccountNumber(), dto.getCatalogId(), type);
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
		String entityId = getEntityId(sapMaterial, soldToCustomer, catalogConfigurationAttributes, type);

		List<QuestionnaireConfiguration> allConfiguredQuestionnaires = questionnaireConfigurationRepository
			.getAllConfiguredQuestionnairesForSpecificType(entityId, type.name(), channelType.name());
		List<Questionnaire> questionnaires = allConfiguredQuestionnaires.stream()
			.map(QuestionnaireConfiguration::getQuestionnaireId)
			.distinct()
			.collect(Collectors.toList());
		return questionnaires;

	}

	private String getEntityId(String materialNumber, String accountNumber,
			CatalogConfigurationAttributes catalogAttributes, QuestionnaireConfigurationType type) {
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
						catalogAttributes.getShipToCustomer(), catalogAttributes.getVariant());
			default:
				throw new IllegalArgumentException("Unknown configuration type: " + type);
		}
	}

	private String getCatalogId(String sapMaterial, String soldToCustomer, String shipToCustomer, String variant) {
		List<String> parts = new ArrayList<>();
		parts.add(sapMaterial);
		parts.add(soldToCustomer);
		parts.add(shipToCustomer);

		if (StringUtils.isNotEmpty(variant)) {
			parts.add(variant);
		}

		// Join the parts with "_"
		return String.join("_", parts);
	}

}
