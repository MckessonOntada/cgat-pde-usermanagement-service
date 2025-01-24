package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.QuestionnaireConfigurationDTO;
import com.mckesson.cgat.pde.usermanagement.model.Questionnaire;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfiguration;
import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
import com.mckesson.cgat.pde.usermanagement.repository.QuestionnaireConfigurationRepository;
import com.mckesson.cgat.pde.usermanagement.repository.QuestionnaireRepository;
import com.mckesson.cgat.pde.usermanagement.service.QuestionnaireService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

		List<QuestionnaireConfiguration> questionnaireConfigurationList = questionnaireList.stream()
			.flatMap(questionId -> questionnaireConfigurationDTO.getEntityIds()
				.stream()
				.map(entityId -> QuestionnaireConfiguration.builder()
					.configuredType(type)
					.questionnaireId(questionId)
					.entityId(entityId)
					.build()))
			.collect(Collectors.toList());

		questionnaireRepository.saveAll(questionnaireList);
		questionnaireConfigurationRepository.saveAll(questionnaireConfigurationList);

	}

}
