package com.mckesson.cgat.pde.usermanagement.service.impl;

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

import java.util.ArrayList;
import java.util.List;

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
	public void configureQuestionnaire(QuestionnaireConfigurationType type,
			QuestionnaireConfigurationDTO questionnaireConfigurationDTO) {
		List<QuestionnaireConfiguration> list = new ArrayList<>();
		for (String questionId : questionnaireConfigurationDTO.getQuestionnarieIds()) {
			for (String entityId : questionnaireConfigurationDTO.getEntityIds()) {
				list.add(QuestionnaireConfiguration.builder()
					.configuredType(type)
					.questionnaireId(questionId)
					.entityId(entityId)
					.build());
			}
		}

		questionnaireConfigurationRepository.saveAll(list);

	}

}
