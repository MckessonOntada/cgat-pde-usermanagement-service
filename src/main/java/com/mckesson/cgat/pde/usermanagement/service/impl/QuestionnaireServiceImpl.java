package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.CatalogConfigurationAttributes;
import com.mckesson.cgat.pde.usermanagement.dto.DeLinkQuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfiguration;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
import com.mckesson.cgat.pde.usermanagement.repository.QuestionnaireConfigurationRepository;
import com.mckesson.cgat.pde.usermanagement.repository.QuestionnaireRepository;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.auth.module.exception.BadRequestException;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
		if (qs != null) {
			questionnaireObj.setId(questionnaireId);
		}
		return questionnaireRepository.save(questionnaireObj);
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

		Map<String, QuestionnaireConfiguration> existingMap = existingConfigs.stream()
			.collect(Collectors.toMap(questionnaireConfiguration -> createQuestionConfigKey(
					questionnaireConfiguration.getQuestionnaireId().getId(), questionnaireConfiguration.getEntityId()),
					Function.identity()));

		List<QuestionnaireConfiguration> questionnaireConfigurationList = questionnaireList.stream()
			.flatMap(questionnaire -> entityIds.stream().map(entityId -> {
				String k = createQuestionConfigKey(questionnaire.getId(), entityId);
				if (existingMap.containsKey(k)) {
					return existingMap.get(k);
				}
				return QuestionnaireConfiguration.builder()
					.configuredType(type)
					.questionnaireId(questionnaire)
					.entityId(entityId)
					.build();

			}))
			.collect(Collectors.toList());

		questionnaireRepository.saveAll(questionnaireList);
		questionnaireConfigurationRepository.saveAll(questionnaireConfigurationList);

	}

	private String createQuestionConfigKey(long questionnaireId, String entityId) {
		return questionnaireId + "_" + entityId;
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
		questionnaireConfigurationRepository
			.deleteByQuestionnaireIdAndEntityIdAndConfiguredType(dto.getQuestionnaireId(), entityId, type);
		long count = questionnaireConfigurationRepository
			.countByQuestionnaireIdAndConfiguredType(dto.getQuestionnaireId(), type);
		if (count == 0) {
			questionnaire.getConfiguredAt().remove(type);
			questionnaireRepository.save(questionnaire);
		}
	}

	@Override
	public List<Questionnaire> getAllConfiguredQuestionnaires(String sapMaterial, String soldToCustomer,
			String shipToCustomer, String variant) {
		String catalogId = getCatalogId(sapMaterial, soldToCustomer, shipToCustomer, variant);
		List<QuestionnaireConfiguration> allConfiguredQuestionnaires = questionnaireConfigurationRepository
			.getAllConfiguredQuestionnaires(sapMaterial, soldToCustomer, catalogId);
		List<Questionnaire> questionnaires = allConfiguredQuestionnaires.stream()
			.map(QuestionnaireConfiguration::getQuestionnaireId)
			.distinct()
			.collect(Collectors.toList());
		return questionnaires;

	}

	@Override
	public Page<Questionnaire> findAll(Pageable paging, String query, List<String> status) {

		String statusActive = CustomUtils.object_status.ALL.name();
		if (!CollectionUtils.isEmpty(status)) {
			if (status.stream().anyMatch(i -> i.equalsIgnoreCase(CustomUtils.object_status.ACTIVE.name()))) {
				statusActive = CustomUtils.object_status.ACTIVE.name();
			}
			if (status.stream().anyMatch(i -> i.equalsIgnoreCase(CustomUtils.object_status.INACTIVE.name()))) {
				statusActive = CustomUtils.object_status.ACTIVE.name().equals(statusActive)
						? CustomUtils.object_status.ALL.name() : CustomUtils.object_status.INACTIVE.name();
			}
		}

		return questionnaireRepository.findAllByQueryText(query, statusActive, paging);
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
			String sapMaterial, String soldToCustomer, String shipToCustomer, String variant) {

		CatalogConfigurationAttributes catalogConfigurationAttributes = CatalogConfigurationAttributes.builder()
			.variant(variant)
			.soldToCustomer(soldToCustomer)
			.shipToCustomer(shipToCustomer)
			.sapMaterial(sapMaterial)
			.build();
		String entityId = getEntityId(sapMaterial, soldToCustomer, catalogConfigurationAttributes, type);

		List<QuestionnaireConfiguration> allConfiguredQuestionnaires = questionnaireConfigurationRepository
			.getAllConfiguredQuestionnairesForSpecificType(entityId, type.name());
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

		if (variant != null) {
			parts.add(variant);
		}

		// Join the parts with "_"
		return String.join("_", parts);
	}

}
