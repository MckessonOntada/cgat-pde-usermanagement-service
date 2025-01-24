package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.DeLinkQuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireConfigurationCatalogDTO;
import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfiguration;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
import com.mckesson.cgat.pde.usermanagement.repository.QuestionnaireConfigurationRepository;
import com.mckesson.cgat.pde.usermanagement.repository.QuestionnaireRepository;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

	QuestionnaireRepository questionnaireRepository;

	QuestionnaireConfigurationRepository questionnaireConfigurationRepository;

	public QuestionnaireServiceImpl(QuestionnaireRepository questionnaireRepository,
			QuestionnaireConfigurationRepository questionnaireConfigurationRepository) {
		this.questionnaireRepository = questionnaireRepository;
		this.questionnaireConfigurationRepository = questionnaireConfigurationRepository;
	}

	public Questionnaire createQuestionnaire(Questionnaire questionnaire) {
		log.info("saving questionnaire");
		return questionnaireRepository.save(questionnaire);
	}

	public List<Questionnaire> getAllQuestionnaires() {
		log.info(" Inside method - getAllQuestionnaires..");
		return questionnaireRepository.findAll();
	}

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
	public void configureQuestionnaireWithMaster(QuestionnaireConfigurationType type,
			QuestionnaireConfigurationDTO questionnaireConfigurationDTO) throws ResourceNotFoundException {
		List<Questionnaire> questionnaireList = questionnaireRepository
			.findAllById(questionnaireConfigurationDTO.getQuestionnaireIds());

		if (questionnaireList.size() != questionnaireConfigurationDTO.getQuestionnaireIds().size()) {
			throw new ResourceNotFoundException("Some Questionnaire Ids not found");
		}

		questionnaireList.forEach(q -> q.getConfiguredAt().add(type));

		List<String> entityIds = generateEntityIds(type, questionnaireConfigurationDTO);

		List<QuestionnaireConfiguration> questionnaireConfigurationList = questionnaireList.stream()
			.flatMap(questionId -> entityIds.stream()
				.map(entityId -> QuestionnaireConfiguration.builder()
					.configuredType(type)
					.questionnaireId(questionId)
					.entityId(entityId)
					.build()))
			.collect(Collectors.toList());

		questionnaireRepository.saveAll(questionnaireList);
		questionnaireConfigurationRepository.saveAll(questionnaireConfigurationList);

	}

	private List<String> generateEntityIds(QuestionnaireConfigurationType type,
			QuestionnaireConfigurationDTO questionnaireConfigurationDTO) {
		switch (type) {
			case Account:
				if (questionnaireConfigurationDTO.getAccountNumberList() == null
						|| questionnaireConfigurationDTO.getAccountNumberList().isEmpty()) {
					throw new IllegalArgumentException("Account Number List should not be null or Empty");
				}
				return questionnaireConfigurationDTO.getAccountNumberList();

			case Material:
				if (questionnaireConfigurationDTO.getMaterialNumberList() == null
						|| questionnaireConfigurationDTO.getMaterialNumberList().isEmpty()) {
					throw new IllegalArgumentException("Material Number List should not be null or Empty");
				}
				return questionnaireConfigurationDTO.getMaterialNumberList();

			case Catalog:
				if (questionnaireConfigurationDTO.getCatalogIdList() == null
						|| questionnaireConfigurationDTO.getCatalogIdList().isEmpty()) {
					throw new IllegalArgumentException("Catalog Id List should not be null or Empty");
				}
				return questionnaireConfigurationDTO.getCatalogIdList()
					.stream()
					.map(catalog -> getCatalogId(catalog))
					.collect(Collectors.toList());

			default:
				throw new IllegalArgumentException("Unknown configuration type: " + type);
		}
	}

	@Override
	@Transactional
	public void deLinkConfigureQuestionnaireWithMaster(DeLinkQuestionnaireConfigurationDTO dto,
			QuestionnaireConfigurationType type) throws ResourceNotFoundException {
		String entityId = getEntityId(dto, type);
		questionnaireConfigurationRepository
			.deleteByQuestionnaireIdAndEntityIdAndConfiguredType(dto.getQuestionnaireId(), entityId, type);
		long count = questionnaireConfigurationRepository
			.countByQuestionnaireIdAndConfiguredType(dto.getQuestionnaireId(), type);
		if (count == 0) {
			Questionnaire questionnaire = questionnaireRepository.findById(dto.getQuestionnaireId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Questionnaire not found with Id: " + dto.getQuestionnaireId()));
			questionnaire.getConfiguredAt().remove(type);
			questionnaireRepository.save(questionnaire);
		}

	}

	private String getEntityId(DeLinkQuestionnaireConfigurationDTO dto, QuestionnaireConfigurationType type) {
		switch (type) {
			case Account:
				if (dto.getAccountNumber() == null)
					throw new IllegalArgumentException("Account Number should not be null");
				return dto.getAccountNumber();
			case Material:
				if (dto.getMaterialNumber() == null)
					throw new IllegalArgumentException("Material Number should not be null");
				return dto.getMaterialNumber();
			case Catalog:
				if (dto.getCatalogId() == null)
					throw new IllegalArgumentException("CatalogId should not be null");
				return getCatalogId(dto.getCatalogId());
			default:
				throw new IllegalArgumentException("Unknown configuration type: " + type);
		}
	}

	private String getCatalogId(QuestionnaireConfigurationCatalogDTO catalog) {
		List<String> parts = new ArrayList<>();
		parts.add(catalog.getSoldTo());
		parts.add(catalog.getShipTo());
		parts.add(catalog.getMaterial());

		if (catalog.getVariant() != null) {
			parts.add(catalog.getVariant());
		}

		// Join the parts with "_"
		return String.join("_", parts);
	}

}
